package com.hitachi.com.klgps;

public class MasterServiceFunction {
    // on Cloud
    //private String urlService = "http://203.154.103.42/KLwebservice/";
    private String urlService = "http://172.23.191.13/KLwebservice/";
    private String methodService = "MethodService.svc";
    //about url web service
    // /InsertTrVehiclesMonitor/{pIMEI}/{pLatitude}/{pLongitude}
    private String InsertTrVehiclesMonitor = urlService + methodService + "/InsertTrVehiclesMonitor";

    public String getInsertTrVehiclesMonitor() {
        return InsertTrVehiclesMonitor;
    }
}
