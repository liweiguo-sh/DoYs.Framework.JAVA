package example.controller;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import doys.framework.core.base.BaseControllerStd;
import doys.framework.core.entity.RestResult;
import doys.framework.util.UtilFile;
import doys.framework.util.UtilYml;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Hashtable;

@RestController
@RequestMapping("/example/barcode")
public class BarcodeController extends BaseControllerStd {
    @RequestMapping("/getBarcode")
    private RestResult getBarcode() {
        int width = 200, height = 200;

        String gtin = in("gtin");
        String mfgDate = in("mfgDate");
        String expDate = in("expDate");

        String path, format = "png";
        String code1D, code2D;
        String file1D, file2D, codeFile;
        // ------------------------------------------------
        try {
            // -- 预处理 --
            path = UtilYml.resTempPath + "/0/";
            if (!UtilFile.checkPath(path)) {
                return ResultErr("创建临时目录失败，请检查。");
            }
            file1D = UtilFile.getFileSN() + ".png";
            file2D = UtilFile.getFileSN() + ".png";
            codeFile = UtilFile.getFileSN() + ".txt";

            // -- write barcode file --
            code1D = (char) 29 + "01" + gtin + (char) 29 + "11" + mfgDate + (char) 29 + "17" + expDate;
            code2D = code1D;

            ArrayList<String> list = new ArrayList<>();
            list.add(code1D);
            list.add(code2D);
            UtilFile.writeFile(path + codeFile, list);

            // -- barcode 2D --
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 2);

            BitMatrix bitMatrix = new MultiFormatWriter().encode(code2D, BarcodeFormat.QR_CODE, width, height, hints);
            Path file = new java.io.File(path + file2D).toPath();
            MatrixToImageWriter.writeToPath(bitMatrix, format, file);

            // -- return --
            code1D = "(01)" + gtin + "(11)" + mfgDate + "(17)" + expDate;
            code2D = code1D;
            ok("code1D", code1D);
            ok("code2D", code2D);

            ok("file1D", file1D);
            ok("file2D", file2D);
            ok("codeFile", codeFile);

            return ResultOk();
        } catch (Exception e) {
            return ResultErr(e);
        }
    }
}
