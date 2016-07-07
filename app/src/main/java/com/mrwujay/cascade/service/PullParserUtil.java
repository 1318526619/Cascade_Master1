package com.mrwujay.cascade.service;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wang on 2016/7/7.
 */
public class PullParserUtil {
    private static final String TAG = "wangshiwei";
    private Context context;
    private XmlPullParser parser;
    private InputStream open;
    private List<String> citys;
    private List<String> districts;

    public PullParserUtil(Context context) {
        this.context = context;
    }

    /**
     * 所有省
     */
    protected List<String> mProvinceDatas;
    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - 市 values - 区
     */
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

    /**
     * key - 区 values - 邮编
     */
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

    /**
     * 当前省的名称
     */
    protected String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;
    /**
     * 当前区的名称
     */
    protected String mCurrentDistrictName = "";

    /**
     * 当前区的邮政编码
     */
    protected String mCurrentZipCode = "";

    public void initData() {
        Log.i(TAG,"beginTime:" + System.currentTimeMillis());
        try {
            parser = XmlPullParserFactory.newInstance().newPullParser();
            open = context.getAssets().open("province_data.xml");
            parser.setInput(open, "utf-8");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        mProvinceDatas = new ArrayList<>();
                        citys = new ArrayList<>();
                        districts = new ArrayList<>();
                        break;

                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("province")) {
                            //省
                            mCurrentProviceName = parser.getAttributeValue(0);
                            mProvinceDatas.add(parser.getAttributeValue(0));
                            citys.clear();
                        } else if (parser.getName().equals("city")) {
                            //市
                            mCurrentCityName = parser.getAttributeValue(0);
                            citys.add(parser.getAttributeValue(0));
                            districts.clear();

                        } else if (parser.getName().equals("district")) {
                            //县、区
                            districts.add(parser.getAttributeValue(0));
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("province")) {
                            //省
                            String[] cityNames = new String[citys.size()];
                            for (int i = 0; i < citys.size(); i++) {
                                cityNames[i] = citys.get(i);
                            }
                            mCitisDatasMap.put(mCurrentProviceName, cityNames);

                        } else if (parser.getName().equals("city")) {
                            //市
                            String[] districtNames = new String[districts.size()];
                            for (int i = 0; i < districts.size(); i++) {
                                districtNames[i] = districts.get(i);
                            }
                            mDistrictDatasMap.put(mCurrentCityName, districtNames);
                        }
                        break;

                }
                eventType = parser.next();
            }
            Log.i(TAG, "overTime:" + System.currentTimeMillis());
            Log.i(TAG, mProvinceDatas.toString());
            Log.i(TAG, mCitisDatasMap.toString());
            Log.i(TAG, mDistrictDatasMap.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
