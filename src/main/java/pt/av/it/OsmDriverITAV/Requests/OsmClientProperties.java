/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
package pt.av.it.OsmDriverITAV.Requests;

public class OsmClientProperties {
    String uri;
    String user;
    String password;
    String project;
    String vimAccount;

    public String getVimAccount() {
        return vimAccount;
    }

    public String getUri() {
        return uri;
    }

    public String getPassword() {
        return password;
    }

    public String getProject() {
        return project;
    }

    public String getUser() {
        return user;
    }
}


