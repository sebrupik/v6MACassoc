package v6macassoc.objects;

class ipv6neighFactory {
    public static ipv6neigh createObject(String command, String input, String source) {
        System.out.println("------------ "+input+" :: "+input.trim().length());
        if(input.trim().length()>0) {
            try {
                switch (command.toLowerCase()) {
                    case "sh ipv6 neigh":
                        return createObjectIOS(command, input, source);
                    case "ip -6 neigh":
                        return createObjectLinux(command, input, source);
                }
            } catch (java.lang.ArrayIndexOutOfBoundsException aiobe) {
                System.out.println("ipv6neighFactory/createObject - "+aiobe.toString());
                System.out.println("ipv6neighFactory/createObject - "+input);
            }
        }
        
        return null;
    }
    
    private static ipv6neigh createObjectIOS(String command, String input, String source) {
        return new ipv6neigh(ipv6FullLength(input.substring(0,42)),
                             Integer.parseInt(input.substring(42,45)),
                             macCompress(input.substring(46,60)),
                             stateNormalise(input.substring(62,67)),
                             input.substring(68, input.length()),
                             source);
    }
    
    private static ipv6neigh createObjectLinux(String command, String input, String source) throws java.lang.ArrayIndexOutOfBoundsException {
        if(input.equals(command))
            return null;
        
        String[] split = input.split("\\s+");
        if(split[3].equals("INCOMPLETE")) {
            return new ipv6neigh(ipv6FullLength(split[0]),
                                 0,
                                 "N/A",
                                 stateNormalise(split[3]),
                                 split[2],
                                 source);
        } else {
            return new ipv6neigh(ipv6FullLength(split[0]),
                                 0,
                                 macCompress(split[4]),
                                 stateNormalise(split[5]),
                                 split[2],
                                 source);
        }
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
            for (int i=0;i<elements.length;i++)
                output+=elements[i];
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