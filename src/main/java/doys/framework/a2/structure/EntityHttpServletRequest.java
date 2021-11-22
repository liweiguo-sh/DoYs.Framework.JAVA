package doys.framework.a2.structure;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // ------------------------------------------------------------------------
    public int getTenantId() {
        int tenantId;
        String tenantIdString = body.getString("tenantId", "0");

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(tenantIdString);

        while (matcher.find()) {
            tenantIdString = matcher.group();
        }
        tenantId = Integer.parseInt(tenantIdString);

        return tenantId;
    }
}