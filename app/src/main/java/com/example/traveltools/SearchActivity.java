package com.example.traveltools;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.interfaces.IRoutePOISearch;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.RouteSearch;
import com.example.traveltools.control.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SearchActivity extends Activity implements View.OnClickListener,
        com.amap.api.maps.AMap.OnMapClickListener, com.amap.api.maps.AMap.OnInfoWindowClickListener, com.amap.api.maps.AMap.InfoWindowAdapter, com.amap.api.maps.AMap.OnMarkerClickListener,
        PoiSearch.OnPoiSearchListener ,AMapLocationListener{
    private com.amap.api.maps.MapView mapview;
    private com.amap.api.maps.AMap mAMap;
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint lp = new LatLonPoint(39.993743, 116.472995);// 116.472995,39.993743
    private Marker locationMarker; // 选择的点
    private Marker detailMarker;
    private Marker mlastMarker;
    private PoiSearch poiSearch;
    private myPoiOverlay poiOverlay;// poi图层
    private List<PoiItem> poiItems;// poi数据

    private RelativeLayout mPoiDetail;
    private TextView mPoiName, mPoiAddress;
    private String keyWord = "";
    private EditText mSearchText;
    private AMapLocationClient mLocationClient = null;
    //    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    private com.amap.api.maps.LocationSource.OnLocationChangedListener mListener = null;
    //    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mapview = (com.amap.api.maps.MapView) findViewById(R.id.mapView);
        mapview.onCreate(savedInstanceState);
        init();
        location();

    }

    /**
     * 初始化AMap对象
     */

    private void init() {
        if (mAMap == null) {
            mAMap = mapview.getMap();
            mAMap.setOnMapClickListener(this);
            mAMap.setOnMarkerClickListener(this);
            mAMap.setOnInfoWindowClickListener(this);
            mAMap.setInfoWindowAdapter(this);
            TextView searchButton = (TextView) findViewById(R.id.btn_search);
            searchButton.setOnClickListener(this);
            locationMarker = mAMap.addMarker(new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.point4)))
                    .position(new com.amap.api.maps.model.LatLng(lp.getLatitude(), lp.getLongitude())));
            locationMarker.showInfoWindow();

        }
        setup();
    }
    private void setup() {
        mPoiDetail = (RelativeLayout) findViewById(R.id.poi_detail);
        mPoiDetail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        mPoiName = (TextView) findViewById(R.id.poi_name);
        mPoiAddress = (TextView) findViewById(R.id.poi_address);
        mSearchText = (EditText)findViewById(R.id.input_edittext);
    }
    /**
     * 开始进行poi搜索
     */
    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }
    protected void doSearchQuery() {
        keyWord = mSearchText.getText().toString().trim();
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
        whetherToShowDetailInfo(false);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapview.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    public void onPoiItemSearched(PoiItem arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        //清除POI信息显示
                        whetherToShowDetailInfo(false);
                        //并还原点击marker样式
                        if (mlastMarker != null) {
                            resetlastmarker();
                        }
                        //清理之前搜索结果的marker
                        if (poiOverlay !=null) {
                            poiOverlay.removeFromMap();
                        }
                        mAMap.clear();
                        poiOverlay = new myPoiOverlay(mAMap, poiItems);
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

                        mAMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(BitmapFactory.decodeResource(
                                                getResources(), R.drawable.point4)))
                                .position(new com.amap.api.maps.model.LatLng(lp.getLatitude(), lp.getLongitude())));
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(SearchActivity.this,
                                "no result");
                    }
                }
            } else {
                ToastUtil
                        .show(SearchActivity.this,"no result");
            }
        } else  {
            ToastUtil.showerror(this.getApplicationContext(), rcode);
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        if (marker.getObject() != null) {
            whetherToShowDetailInfo(true);
            try {
                PoiItem mCurrentPoi = (PoiItem) marker.getObject();
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    resetlastmarker();
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.drawable.poi_marker_pressed)));
                setPoiItemDisplayContent(mCurrentPoi);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }else {
            whetherToShowDetailInfo(false);
            resetlastmarker();
        }


        return true;
    }

    // 将之前被点击的marker置为原来的状态
    private void resetlastmarker() {
        int index = poiOverlay.getPoiIndex(mlastMarker);
        if (index < 10) {
            mlastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            markers[index])));
        }else {
            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
        }
        mlastMarker = null;

    }


    private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
        mPoiName.setText(mCurrentPoi.getTitle());
        mPoiAddress.setText(mCurrentPoi.getSnippet()+mCurrentPoi.getDistance());
    }


    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                doSearchQuery();
                break;

            default:
                break;
        }

    }

    private int[] markers = {R.drawable.poi_marker_1,
            R.drawable.poi_marker_2,
            R.drawable.poi_marker_3,
            R.drawable.poi_marker_4,
            R.drawable.poi_marker_5,
            R.drawable.poi_marker_6,
            R.drawable.poi_marker_7,
            R.drawable.poi_marker_8,
            R.drawable.poi_marker_9,
            R.drawable.poi_marker_10
    };

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mPoiDetail.setVisibility(View.VISIBLE);

        } else {
            mPoiDetail.setVisibility(View.GONE);

        }
    }


    @Override
    public void onMapClick(com.amap.api.maps.model.LatLng arg0) {
        whetherToShowDetailInfo(false);
        if (mlastMarker != null) {
            resetlastmarker();
        }
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(this, infomation);

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                lp.setLatitude(aMapLocation.getLatitude());//获取纬度
                lp.setLongitude(aMapLocation.getLongitude());//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getStreet();//街道信息
                aMapLocation.getStreetNum();//街道门牌号信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    mAMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
                    mAMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(aMapLocation);
                    //添加图钉
                    //  aMap.addMarker(getMarkerOptions(amapLocation));
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(aMapLocation.getCountry() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getCity() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getDistrict() + ""
                            + aMapLocation.getStreet() + ""
                            + aMapLocation.getStreetNum());
                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                    isFirstLoc = false;
                }


            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                Toast.makeText(getApplicationContext(), "定位失败，请确认是否开启GPS定位权限", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * 自定义PoiOverlay
     *
     */

    private class myPoiOverlay {
        private com.amap.api.maps.AMap mamap;
        private List<PoiItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
        public myPoiOverlay(com.amap.api.maps.AMap amap , List<PoiItem> pois) {
            mamap = amap;
            mPois = pois;
        }

        /**
         * 添加Marker到地图中。
         * @since V2.1.0
         */
        public void addToMap() {
            if(mPois != null) {
                int size = mPois.size();
                for (int i = 0; i < size; i++) {
                    Marker marker = mamap.addMarker(getMarkerOptions(i));
                    PoiItem item = mPois.get(i);
                    marker.setObject(item);
                    mPoiMarks.add(marker);
                }
            }
        }

        /**
         * 去掉PoiOverlay上所有的Marker。
         *
         * @since V2.1.0
         */
        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
            }
        }

        /**
         * 移动镜头到当前的视角。
         * @since V2.1.0
         */
        public void zoomToSpan() {
            if (mPois != null && mPois.size() > 0) {
                if (mamap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                mamap.moveCamera(com.amap.api.maps.CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            if(mPois != null) {
                int size = mPois.size();
                for (int i = 0; i < size; i++) {
                    b.include(new com.amap.api.maps.model.LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                            mPois.get(i).getLatLonPoint().getLongitude()));
                }
            }
            return b.build();
        }

        private MarkerOptions getMarkerOptions(int index) {
            return new MarkerOptions()
                    .position(
                            new com.amap.api.maps.model.LatLng(mPois.get(index).getLatLonPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(index));
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            return mPois.get(index).getSnippet();
        }

        /**
         * 从marker中得到poi在list的位置。
         *
         * @param marker 一个标记的对象。
         * @return 返回该marker对应的poi在list的位置。
         * @since V2.1.0
         */
        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 返回第index的poi的信息。
         * @param index 第几个poi。
         * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
         * @since V2.1.0
         */
        public PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            if (arg0 < 10) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), markers[arg0]));
                return icon;
            }else {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight));
                return icon;
            }
        }
    }

}

//        extends AppCompatActivity implements LocationSource, AMapLocationListener {
//
//    //AMap是地图对象
//    private AMap aMap;
//    private MapView mapView;
//    private String weatherbaseurl ="https://restapi.amap.com/v3/weather/weatherInfo?city=";
//    //声明AMapLocationClient类对象，定位发起端
//    private AMapLocationClient mLocationClient = null;
//    //声明mLocationOption对象，定位参数
//    public AMapLocationClientOption mLocationOption = null;
//    //声明mListener对象，定位监听器
//    private OnLocationChangedListener mListener = null;
//    //标识，用于判断是否只显示一次定位信息和用户重新定位
//    private boolean isFirstLoc = true;
//    //显示定位蓝点
//    private MyLocationStyle myLocationStyle;
//    private ImageView back;
//    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
//    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
//
//    //-----------
////    ViewFlipper viewFlipper;
//    //    int[] ids={R.drawable.bg1,R.drawable.bg2,R.drawable.bg3,R.drawable.bg4};
//    //------------------------
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search);
//        //获取地图控件引用
//        mapView = (MapView) findViewById(R.id.map);
//        myLocationStyle = new MyLocationStyle();
//
//        myLocationStyle.strokeColor(STROKE_COLOR);
//        //自定义精度范围的圆形边框宽度
//        myLocationStyle.strokeWidth(5);
//        // 设置圆形的填充颜色
//        myLocationStyle.radiusFillColor(FILL_COLOR);
//
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) ;//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
//        myLocationStyle.showMyLocation(true);
//        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
//        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
//        mapView.onCreate(savedInstanceState);
//        if (aMap == null) {
//            aMap = mapView.getMap();
//            //设置显示定位按钮 并且可以点击
//            UiSettings settings = aMap.getUiSettings();
//            aMap.setMyLocationStyle(myLocationStyle);
//            aMap.setMyLocationEnabled(true);
//            aMap.setLocationSource( this);//设置了定位的监听
//            // 是否显示定位按钮
//            settings.setMyLocationButtonEnabled(true);
//            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
//        }
//        //开始定位
//        location();
//        back=(ImageView)findViewById(R.id.searchback);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//
//
////        viewFlipper=(ViewFlipper) findViewById(R.id.weatherflipper);
//
////        for (int i = 0; i < ids.length; i++) {
////            ImageView iview= getImageView(ids[i]);
////            viewFlipper.addView(iview);
////        }
////        viewFlipper.setInAnimation(this,R.anim.push_left_in);
////        viewFlipper.setOutAnimation(this,R.anim.push_left_out);
////        //设置多少秒切换
////        viewFlipper.setFlipInterval(2000);
////        viewFlipper.startFlipping();
//    }
////    private ImageView getImageView(int id) {
////        ImageView imageView=new ImageView(this);
////        imageView.setBackgroundResource(id);
////        return imageView;
////    }
//
//
//    private void location() {
//        //初始化定位
//        mLocationClient = new AMapLocationClient(getApplicationContext());
//        //设置定位回调监听
//        mLocationClient.setLocationListener(this);
//        //初始化定位参数
//        mLocationOption = new AMapLocationClientOption();
//        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        //设置是否返回地址信息（默认返回地址信息）
//        mLocationOption.setNeedAddress(true);
//        //设置是否只定位一次,默认为false
//        mLocationOption.setOnceLocation(false);
//        //设置是否强制刷新WIFI，默认为强制刷新
//        mLocationOption.setWifiActiveScan(true);
//        //设置是否允许模拟位置,默认为false，不允许模拟位置
//        mLocationOption.setMockEnable(false);
//        //设置定位间隔,单位毫秒,默认为2000ms
//        mLocationOption.setInterval(2000);
//        //给定位客户端对象设置定位参数
//        mLocationClient.setLocationOption(mLocationOption);
//        //启动定位
//        mLocationClient.startLocation();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
//        mapView.onDestroy();
//        mLocationClient.stopLocation();//停止定位
//        mLocationClient.onDestroy();//销毁定位客户端。
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
//        mapView.onPause();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
//        mapView.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onLocationChanged(AMapLocation aMapLocation) {
//        if (aMapLocation != null) {
//            if (aMapLocation.getErrorCode() == 0) {
//                //定位成功回调信息，设置相关消息
//                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
//                aMapLocation.getLatitude();//获取纬度
//                aMapLocation.getLongitude();//获取经度
//                aMapLocation.getAccuracy();//获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(aMapLocation.getTime());
//                df.format(date);//定位时间
//                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                aMapLocation.getCountry();//国家信息
//                aMapLocation.getProvince();//省信息
//                aMapLocation.getCity();//城市信息
//                aMapLocation.getDistrict();//城区信息
//                aMapLocation.getStreet();//街道信息
//                aMapLocation.getStreetNum();//街道门牌号信息
//                aMapLocation.getCityCode();//城市编码
//                aMapLocation.getAdCode();//地区编码
//
//                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
//                if (isFirstLoc) {
//                    //设置缩放级别
//                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
//                    //将地图移动到定位点
//                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
//                    //点击定位按钮 能够将地图的中心移动到定位点
//                    mListener.onLocationChanged(aMapLocation);
//                    //添加图钉
//                    //  aMap.addMarker(getMarkerOptions(amapLocation));
//                    //获取定位信息
//                    StringBuffer buffer = new StringBuffer();
//                    buffer.append(aMapLocation.getCountry() + ""
//                            + aMapLocation.getProvince() + ""
//                            + aMapLocation.getCity() + ""
//                            + aMapLocation.getProvince() + ""
//                            + aMapLocation.getDistrict() + ""
//                            + aMapLocation.getStreet() + ""
//                            + aMapLocation.getStreetNum());
//                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
//                    isFirstLoc = false;
//                }
//
//
//            } else {
//                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
//                Log.e("AmapError", "location Error, ErrCode:"
//                        + aMapLocation.getErrorCode() + ", errInfo:"
//                        + aMapLocation.getErrorInfo());
//                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//
//    @Override
//    public void activate(OnLocationChangedListener onLocationChangedListener) {
//        mListener = onLocationChangedListener;
//    }
//
//    @Override
//    public void deactivate() {
//        mListener = null;
//    }
//}