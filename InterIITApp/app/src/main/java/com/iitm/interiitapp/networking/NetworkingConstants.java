package com.iitm.interiitapp.networking;

import com.iitm.interiitapp.objects.WasteRequestObject;

public final class NetworkingConstants {


    private static final String BASE_DOMAIN = "http://10.70.23.214:8000";
    public static final String WASTE_HISTORY_URL = BASE_DOMAIN + "/get_waste_logs";
    public static final String LOGIN_USER_URL = BASE_DOMAIN + "/login_user";
    public static final String LOGIN_SCRAP_COLLECTOR_URL = BASE_DOMAIN + "/login_scrap_collector";
    public static final String NEW_WASTE_REQUEST_URL = BASE_DOMAIN + "/new_waste_request";
    public static final String NEAR_BY_REQUEST_URL = BASE_DOMAIN + "/get_scrap_collectors";
    public static final String NEW_SCRAP_COLLECTOR_URL = BASE_DOMAIN + "/new_scrap_collector";
    public static final String NEW_USER_URL = BASE_DOMAIN + "/new_user";
    public static final String ROUTE_URL = BASE_DOMAIN + "/get_uncollected_waste";
    public static final String PICK_UP_WASTE_URL = BASE_DOMAIN + "/waste_collected";
    public static final String INVENTORY_URL = BASE_DOMAIN + "/get_inventory";

    public static String getLoginBody(String phone, String password){
        return "{" +
                    "\"mobile\": \""+ phone +"\"," +
                    "\"password\": \"" + password +"\""+
                "}";
    }


    public static String getWasteRequestBody(WasteRequestObject requestObject) {
        return "{" +
                "\"mobile\": \"" + requestObject.mobile +"\","+
                "\"amount\": \"" + requestObject.amount +"\","+
                "\"lat\": " + requestObject.lat +","+
                "\"long\": " + requestObject.lng +","+
                "\"waste_type\": " + requestObject.wasteType+","+
                "\"comments\":\"" + requestObject.comments+"\""+
            "}";
    }

    public static String getNearByRequestBody(double lat, double lng) {
        String s = "{" +
                "\"lat\": " + lat +","+
                "\"long\": " + lng +"" +
                "}";
        return  s;
    }

    public static String getNewScrapCollectorRequest(String mobile, String password, String name, String email, String description, String lisenceNo, double vehicleCap, double lat, double lng) {
        return "{" +
                "\"name\":\""+name+"\"," +
                "\"mobile\":\""+mobile+"\"," +
                "\"password\":\""+ password+"\"," +
                "\"email\":\""+ email+"\"," +
                "\"lisenceNo\":\""+ lisenceNo+"\"," +
                "\"description\":\""+ description+"\"," +
                "\"vehicle_cap\":"+ vehicleCap+"," +
                "\"loc_lat\":"+lat+"," +
                "\"loc_long\":"+ lng +
                "}";
    }
}
