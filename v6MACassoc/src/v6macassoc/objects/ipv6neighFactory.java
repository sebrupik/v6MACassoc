package v6macassoc.objects;

class ipv6neighFactory {
    public static ipv6neigh createObject(String command, String input, String source) {
        switch (command.toLowerCase()) {
            case "sh ipv6 neigh":
                return createObjectIOS(command, input, source);
        }
        
        return null;
    }
    
    private static ipv6neigh createObjectIOS(String command, String input, String source) {
        String ipv6_address = input.substring(0,42);
        int age = Integer.parseInt(input.substring(42,45));
        String mac_address = input.substring(46,60);
        String state = input.substring(62,67);
        String interf = input.substring(68, input.length());
        
        
        
        return 
    }
    
    private static ipv6neigh createObjectLinux(String command, String input, String source) {
        
    }
    
    private static String ipv6FullLength(String oriIPv6) {
        String[] elements = oriIPv6.split(":");
        byte[] byte_order = new byte[elements.length];
        
        for(int i=0;i<elements.length; i++) {
            byte[i] = elements[i].get
        }
    }
}
