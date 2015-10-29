package v6macassoc.objects;

class ipv6neighFactory {
    public static ipv6neigh createObject(String command, String input, String source, long timestamp) {
        //System.out.println("------------ "+input+" :: "+input.trim().length());
        if(input.trim().length()>0) {
            if(input.equals(command))
                return null;
            if(input.startsWith("IPv6"))
                return null;
            try {
                switch (command.toLowerCase()) {
                    case "sh ipv6 neigh":
                        return createObjectIOS(command, input, source, timestamp);
                    case "ip -6 neigh":
                        return createObjectLinux(command, input, source, timestamp);
                }
            System.out.println("ipv6neighFactory/createObject - finished parsing input");    
            } catch (java.lang.ArrayIndexOutOfBoundsException aiobe) {
                System.out.println("ipv6neighFactory/createObject - "+aiobe.toString());
                System.out.println("ipv6neighFactory/createObject - "+input);
            }
        }
        
        return null;
    }
    
    private static ipv6neigh createObjectIOS(String command, String input, String source, long timestamp) {
        System.out.println("ipv6neighFactory/createObjectIOS - about to parse input");
        return new ipv6neigh(ipv6FullLength(input.substring(0,42)),
                             Integer.parseInt(input.substring(42,45).trim()),
                             macCompress(input.substring(46,60)),
                             stateNormalise(input.substring(62,67)),
                             input.substring(68, input.length()),
                             source, timestamp);
    }
    
    private static ipv6neigh createObjectLinux(String command, String input, String source, long timestamp) throws java.lang.ArrayIndexOutOfBoundsException {
        boolean route = false;
        
        String[] split = input.split("\\s+");
        
        /* 
        The linux 'ip -6 neigh' occasionally throws up lines containing items 'route' or 'router'.
        'Router' onces tend to be the linux boxes interfaces, but sometimes there are other MAC addresses listed, these maybe rogue routets?
        'route' not sure what these are?!
        */
        if(command.equals("ip -6 neigh")) {
            for(String element : split) {
            //for(int i=0;i<split.length;i++) {
                if(element.startsWith("route")) {
                    route = true;
                    System.out.println("found a strange one! : "+input);
                    break;
                }
            }
        }
        if(!route) {
            if(split[3].equals("INCOMPLETE") | split[3].equals("FAILED")) {
                return new ipv6neigh(ipv6FullLength(split[0]),
                                     0,
                                     "N/A",
                                     stateNormalise(split[3]),
                                     split[2],
                                     source, timestamp);
            } else {
                return new ipv6neigh(ipv6FullLength(split[0]),
                                     0,
                                     macCompress(split[4]),
                                     stateNormalise(split[5]),
                                     split[2],
                                     source, timestamp);
            }
        }
        return null;
    }
    
    private static String ipv6FullLength(String oriIPv6) {
        StringBuilder sb = new StringBuilder(oriIPv6.trim());
        int index = oriIPv6.indexOf("::");
        if(index!=-1) {
            int octets = (oriIPv6.split(":").length)-1;
            String insert="";
            for(int i=octets;i<8; i++) {
                insert+="0000";
                if(i<=6)
                    insert+=":";
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
        return output;
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
            for(String element : elements) 
                output+=element;
        }

	if (output.length()!=12) {
	    System.out.println("something has gone wrong with the parse on "+oriMAC+" ("+output+"), returning null");
            output=null;
	}
        return output;
    }
    
    private static String stateNormalise(String state) {
        switch(state) {
            case "INCOMP":
                return "INCOMPLETE";
            case "REACH":
                return "REACHABLE";
        }
        return state;
    }
}