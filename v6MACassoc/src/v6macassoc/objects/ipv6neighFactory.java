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
        StringBuilder sb = new StringBuilder(oriIPv6);
        int index = oriIPv6.indexOf("::");
        if(index!=-1) {
            int octets = (oriIPv6.split(":").length)-1;
            String insert="";
            for(int i=octets;i<8; i++) {
                insert+="0000";
                if(i<=6)
                    insert+=":";
		System.out.println("insert == "+insert);
            }
            sb.insert(index+1, insert);
        }
	
        String[] elements = sb.toString().split(":");
        String output ="";
        for(int i=0;i<elements.length; i++) {
            while(elements[i].length()!=4)
                elements[i] = "0"+elements[i];
	    if(i==0)
		output = elements[i];
	    else
               output = output+":"+elements[i];
        }
    }
    
    private static String macCompress(String oriMAC) {
        String output="";
        String[] elements = null;
        if(oriMAC.contains(".")) {  // IOS
            elements = oriMAC.split("\\.");
        } else if(oriMAC.contains(":")) {  //linux
             elements = oriMAC.split(":");
        }
        if(elements!=null) {
            for (int i=0;i<elements.length;i++)
                output+=elements[i];
        }

	if (output.length()!=12) {
	    System.out.println("something hhas gone wrong with the parse on "+oriMAC+" ("+output+"), returning null");
            output=null;
	}
        return output;
    }
}
