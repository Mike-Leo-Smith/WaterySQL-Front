class EngineJNI {
    
    public native static void initialize();
    public native static void finish();
    public native static void execute(String command);
    
    static {
        System.loadLibrary("WaterySQLEngineJNI");
    }
    
}
