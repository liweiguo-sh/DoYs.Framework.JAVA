package doys.framework.a2.structure;
import doys.framework.util.UtilFile;
public class EntityUploadFile {
    private String _path = "";
    private String _name = "";
    private String _pathname = "";
    private String _originalName = "";
    private String _url = "";
    // ------------------------------------------------------------------------
    public EntityUploadFile() {
    }

    // ------------------------------------------------------------------------
    public void setPath(String path) {
        _path = path;
        if (!_name.equals("")) {
            _pathname = UtilFile.Combine(_path, _name);
        }
    }
    public void setName(String name) {
        _name = name;
        if (!_path.equals("")) {
            _pathname = UtilFile.Combine(_path, _name);
        }
    }
    public void setOriginalName(String name) {
        _originalName = name;
    }

    // ------------------------------------------------------------------------
    public String getPath() {
        return _path;
    }
    public String getName() {
        return _name;
    }
    public String getPathname() {
        return _pathname;
    }
    public String getOriginalName() {
        return _originalName;
    }
}