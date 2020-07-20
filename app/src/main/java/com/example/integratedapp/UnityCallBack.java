package com.example.integratedapp;

public class UnityCallBack {
    private static final UnityCallBack ourInstance = new UnityCallBack();
    private static String jsonString;
    private static boolean useDummyValues;
    private static boolean modelLoadScene;


    public static UnityCallBack getInstance() {
        return ourInstance;
    }

    private UnityCallBack() {
    }

    public String jSONStringToUnity(){
        return jsonString;
    }
    public boolean createDummyModel(){
        return useDummyValues;
    }
    public boolean openModelLoadScene(){
        return modelLoadScene;
    }

    public void setJsonString(String jsonString){
        UnityCallBack.jsonString = jsonString;
    }

//    public static void setUseDummyValues(boolean useDummyValues){
//        UnityCallBack.useDummyValues = useDummyValues;
//    }

    public static void setModelLoadScene(boolean modelLoadScene){
        UnityCallBack.modelLoadScene = modelLoadScene;
    }

}
