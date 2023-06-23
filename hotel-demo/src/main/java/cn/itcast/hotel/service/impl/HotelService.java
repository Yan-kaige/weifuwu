package cn.itcast.hotel.service.impl;

import cn.itcast.hotel.mapper.HotelMapper;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public PageResult search(RequestParams params) {

        SearchRequest request = new SearchRequest("hotel");
        request.source().query(buildQuery(params));
        request.source().from((params.getPage()-1)*params.getSize()).size(params.getSize());


        String location = params.getLocation();
        if (StringUtils.isNotEmpty(location)){
            GeoPoint geoPoint = new GeoPoint(location);
            request.source().sort(SortBuilders.geoDistanceSort("location", geoPoint)
                    .order(SortOrder.ASC)
                    .unit(DistanceUnit.KILOMETERS));
        }

        SearchResponse search = null;
        try {
            search = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SearchHit[] hits = search.getHits().getHits();

        ArrayList<HotelDoc> hotelDocs = new ArrayList<>();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(sourceAsString, HotelDoc.class);

            Object[] sortValues = hit.getSortValues();
            if (sortValues.length>0){
                hotelDoc.setDistance( sortValues[0]);
            }
            hotelDocs.add(hotelDoc);
        }


        return new PageResult(search.getHits().getTotalHits().value,hotelDocs);
    }

    private static FunctionScoreQueryBuilder buildQuery(RequestParams params) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        String key = params.getKey();
        if (StringUtils.isEmpty(key)){
            boolQuery.must(QueryBuilders.matchAllQuery());
        }else {
            boolQuery.must(QueryBuilders.matchQuery("all",key));
        }

        if(!StringUtils.isEmpty(params.getCity())){
            boolQuery.filter(QueryBuilders.termQuery("city", params.getCity()));
        }

        if(!StringUtils.isEmpty(params.getBrand())){
            boolQuery.filter(QueryBuilders.termQuery("brand", params.getBrand()));
        }

        if(!StringUtils.isEmpty(params.getStarName())){
            boolQuery.filter(QueryBuilders.termQuery("starName", params.getStarName()));
        }

        if(params.getMinPrice()!=null & params.getMaxPrice()!=null){
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(params.getMinPrice()).lte(params.getMaxPrice()));
        }


        FunctionScoreQueryBuilder queryBuilder = QueryBuilders.functionScoreQuery(boolQuery,
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                QueryBuilders.termQuery("isAD", true),
                                ScoreFunctionBuilders.weightFactorFunction(10))
                });
        return queryBuilder;
    }

    @Override
    public Map<String, List<String>> getFilters(RequestParams params) {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(buildQuery(params));

        request.source().size(0);
        request.source().aggregation(
                AggregationBuilders.terms("brandAgg")
                        .field("brand")
                        .size(10)
        );
        request.source().aggregation(
                AggregationBuilders.terms("cityAgg")
                        .field("city")
                        .size(10)
        );
        request.source().aggregation(
                AggregationBuilders.terms("starAgg")
                        .field("starName")
                        .size(10)
        );
        SearchResponse search = null;
        try {
            search = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HashMap<String, List<String>> res = new HashMap<>();

        res.put("brand",getAgg(search,"brandAgg"));
        res.put("city",getAgg(search,"cityAgg"));
        res.put("starName",getAgg(search,"starAgg"));

        return res;

    }

    @Override
    public List<String> getSuggestions(String prefix) {
        try {
            // 1.准备Request
            SearchRequest request = new SearchRequest("hotel");
            // 2.准备DSL
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(prefix)
                            .skipDuplicates(true)
                            .size(10)
            ));
            // 3.发起请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // 4.解析结果
            Suggest suggest = response.getSuggest();
            // 4.1.根据补全查询名称，获取补全结果
            CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
            // 4.2.获取options
            List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
            // 4.3.遍历
            List<String> list = new ArrayList<>(options.size());
            for (CompletionSuggestion.Entry.Option option : options) {
                String text = option.getText().toString();
                list.add(text);
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertById(Long id) {
        try {
            // 0.根据id查询酒店数据
            Hotel hotel = getById(id);
            // 转换为文档类型
            HotelDoc hotelDoc = new HotelDoc(hotel);

            // 1.准备Request对象
            IndexRequest request = new IndexRequest("hotel").id(hotel.getId().toString());
            // 2.准备Json文档
            request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);
            // 3.发送请求
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteById(Long id) {
        try {
            // 1.准备Request
            DeleteRequest request = new DeleteRequest("hotel", id.toString());
            // 2.发送请求
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<String> getAgg(SearchResponse search,String name) {
        Aggregations aggregations = search.getAggregations();
        Terms brandAgg = aggregations.get(name);


        ArrayList<String> temp = new ArrayList<>();
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            temp.add(bucket.getKeyAsString());
        }
        return temp;
    }
}
