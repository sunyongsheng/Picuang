package top.aengus.panther.tool;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * <h3>picuang</h3>
 * <p>下载文件类</p>
 *
 * @author : https://github.com/AdlerED
 * @date : 2019-11-07 23:24
 **/
public class DownloadUtil {
    /**
     * 下载项目根目录下doc下的文件
     *
     * @param response response
     * @param fileName 文件名
     */
    public static void downloadFile(HttpServletResponse response, String fileName) {
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os;
        try {
            os = response.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(fileName));
            int i;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e3) {
            e3.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException IOE) {
                    IOE.printStackTrace();
                }
            }
        }
    }
}
