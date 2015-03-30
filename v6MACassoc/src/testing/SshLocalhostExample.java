//https://groups.google.com/forum/#!topic/java-expectit/tgniPYoE25k

import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;
import static net.sf.expectit.matcher.Matchers.contains;
import static net.sf.expectit.matcher.Matchers.regexp;
import static net.sf.expectit.matcher.Matchers.times;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * An example of interacting with the local SSH server
 */
public class SshLocalhostExample {
    public static void main(String[] args) throws JSchException, IOException {
        JSch jSch = new JSch();
        Session session = jSch.getSession("v6macassoc", "localhost", 2221);
        session.setPassword("password");
        
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        //jSch.addIdentity(System.getProperty("user.home") + "/.ssh/id_rsa");
        session.setConfig(config);
        session.connect();
        Channel channel = session.openChannel("shell");
        channel.connect();
        
        Expect expect = new ExpectBuilder()
                .withOutput(channel.getOutputStream())
                .withInputs(channel.getInputStream(), channel.getExtInputStream())
                .build();
        
        try {
            expect.expect(contains("$"));
            expect.sendLine("ip -6 neigh");
            
            //System.out.println("pwd1:" + expect.expect(contains("ip -6 neigh")).getBefore());
            /*System.out.println(
                    "pwd1:" + expect.expect(times(2, contains("\n")))
                            .getResults()
                            .get(1)
                            .getBefore());*/
            /*expect.sendLine("pwd");
            // a regexp which captures the output of pwd
            System.out.println("pwd2:" + expect.expect(regexp("(?m)\\n([^\\n]*)\\n")).group(1));
            expect.expect(contains("$"));
            expect.sendLine("ip -6 neigh");
            // skipping the echo command
            expect.expect(times(2, contains("\n")));
            // getting the output of ls
            System.out.println(expect.expect(regexp(".*\\$")).getBefore().trim());*/
            expect.sendLine("exit");
            
            BufferedReader buff = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            String line;
            while(true) {
                line = buff.readLine();
                if (line==null)
                    break;
                System.out.println(line);
            }
            
        } finally {
            expect.close();
            channel.disconnect();
            session.disconnect();
        }
    }
}
