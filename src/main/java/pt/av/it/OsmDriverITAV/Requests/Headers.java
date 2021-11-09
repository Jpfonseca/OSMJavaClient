/**
 * @author João Fonseca (jpedrofonseca@av.it.pt)
 */
package pt.av.it.OsmDriverITAV.Requests;

public enum Headers {
    TEXT("text/plain"),
    YML("application/yaml"),
    JSON("application/json"),
    ZIP("application/zip")
    ;

    private String headerInfo;

    Headers(String header){
        this.headerInfo=header;
    }

    public String getHeaderInfo() {
        return headerInfo;
    }
}
