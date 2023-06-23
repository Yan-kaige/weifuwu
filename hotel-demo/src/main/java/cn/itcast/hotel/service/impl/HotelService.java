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
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public PageResult search(RequestParams params) {

        SearchRequest request = new SearchRequest("hotel");

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        String key = params.getKey();
        if (StringUtils.isEmpty(key)){
            boolQuery.must(QueryBuilders.matchAllQuery());
        }else {
            boolQuery.must(QueryBuilders.matchQuery("all",key));
        }

        if(!StringUtils.isEmpty(params.getCity())){
            boolQuery.filter(QueryBuilders.termQuery("city",params.getCity()));
        }

        if(!StringUtils.isEmpty(params.getBrand())){
            boolQuery.filter(QueryBuilders.termQuery("brand",params.getBrand()));
        }

        if(!StringUtils.isEmpty(params.getStarName())){
            boolQuery.filter(QueryBuilders.termQuery("starName",params.getStarName()));
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

        request.source().query(queryBuilder);
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
}
