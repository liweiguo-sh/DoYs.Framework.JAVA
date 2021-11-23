package doys.framework.a2.structure;
import javax.servlet.http.HttpServletRequest;

public class EntityHttpServletRequest {
    public EntityHttpServletRequestHeader header;
    public EntityHttpServletRequestBody body;

    // ------------------------------------------------------------------------
    public EntityHttpServletRequest(HttpServletRequest request) {
        try {
            body = new EntityHttpServletRequestBody(request);
            header = new EntityHttpServletRequestHeader(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}