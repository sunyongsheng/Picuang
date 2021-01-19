package pers.adlered.picuang.controller.api;

import org.springframework.stereotype.Controller;

/**
 * <h3>picuang</h3>
 * <p>查看历史记录API</p>
 *
 * @author : https://github.com/AdlerED
 * @date : 2019-11-06 16:24
 **/
@Controller
public class HistoryController {
//    @RequestMapping("/api/list")
//    @ResponseBody
//    public List<PicProp> list(HttpServletRequest request, String year, String month, String day) {
//        List<PicProp> list = new ArrayList<>();
//        File file = new File(getHome());
//        Date curr = new Calendar.Builder()
//                .setDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day))
//                .build().getTime();
//        Date next = new Calendar.Builder()
//                .setDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day + 1))
//                .build().getTime();
//        try {
//            listFiles(list, file, curr, next, request);
//        } catch (NullPointerException ignored) {
//        }
//        return list;
//    }
//
//    private void listFiles(List<PicProp> list, File root, Date curr, Date next, HttpServletRequest request) {
//        Date date = new Date(root.lastModified());
//        if (date.before(curr) || date.after(next)) return;
//        if (root.isDirectory() && root.listFiles() != null) {
//            for (File f : root.listFiles()) {
//                if (f.isFile()) {
//                    PicProp picProp = new PicProp();
//                    picProp.setTime(DateFormatter.hourColonMinFormat(new Date(f.lastModified())));
//                    picProp.setFilename(f.getName());
//                    picProp.setPath(f.getAbsolutePath().substring(Prop.savePath().length() - 1));
//                    picProp.setIp(IPUtil.getIpAddr(request));
//                    list.add(picProp);
//                } else {
//                    listFiles(list, f, curr, next, request);
//                }
//            }
//        } else if (root.isFile()) {
//            PicProp picProp = new PicProp();
//            picProp.setTime(DateFormatter.hourColonMinFormat(new Date(root.lastModified())));
//            picProp.setFilename(root.getName());
//            picProp.setPath(root.getAbsolutePath().substring(Prop.savePath().length() - 1));
//            picProp.setIp(IPUtil.getIpAddr(request));
//            list.add(picProp);
//        }
//    }
//
//    @RequestMapping("/api/day")
//    @ResponseBody
//    public List<String> day(String year, String month) {
//        StringBuilder sb = new StringBuilder();
//        File file = new File(getHome() + year + "/" + month + "/");
//        File[] list = file.listFiles();
//        List<String> lists = new ArrayList<>();
//        try {
//            for (File i : list) {
//                if (i.isDirectory()) {
//                    lists.add(i.getName());
//                }
//            }
//        } catch (NullPointerException NPE) {
//        }
//        return lists;
//    }
//
//    @RequestMapping("/api/month")
//    @ResponseBody
//    public List<String> month(String year) {
//        StringBuilder sb = new StringBuilder();
//        File file = new File(getHome() + year + "/");
//        File[] list = file.listFiles();
//        List<String> lists = new ArrayList<>();
//        try {
//            for (File i : list) {
//                if (i.isDirectory()) {
//                    lists.add(i.getName());
//                }
//            }
//        } catch (NullPointerException ignored) {
//        }
//        return lists;
//    }
//
//    @RequestMapping("/api/year")
//    @ResponseBody
//    public List<String> year() {
//        StringBuilder sb = new StringBuilder();
//        File file = new File(getHome());
//        File[] list = file.listFiles();
//        List<String> lists = new ArrayList<>();
//        try {
//            for (File i : list) {
//                if (i.isDirectory()) {
//                    lists.add(i.getName());
//                }
//            }
//        } catch (NullPointerException ignored) {
//        }
//        return lists;
//    }
//
//    private String getHome() {
//        return FileUtil.ensureSuffix(Prop.savePath());
//    }
}
