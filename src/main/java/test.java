//package com.codility;
//
//public class StringPathChanger {
//    public Class<IllegalStateException> changeDirectoryString(String currentDirectory, String command) {
//        String[] strList = currentDirectory.substring(1).split("/");
//        String cond = command.split(" ")[1];
//        try{
//            if (cond.equals("..") || cond.equals("../")){
//                StringBuilder res = new StringBuilder("");
//                for (int i = 0; i < strList.length - 1; i++) {
//                    res.append("/").append(strList[i]);
//                }
//                return res.toString();
//            }
//            else if (cond.matches("^[a-zA-Z]+$")){
//                StringBuilder res = new StringBuilder("");
//                for (int i = 0; i < strList.length; i++) {
//                    res.append("/").append(strList[i]);
//                }
//                return res.toString() + "/" + cond;
//            }
//            else if (cond.equals("/"))
//                return "/";
//            else if (cond.charAt(0) == '/' && cond.length() > 1)
//            {
//                return  cond;
//            }
//            else if (cond.contains("../")){
//                String[] t = cond.split("/");
//                int c = 0;
//                for (int k = 0; k < t.length; k++){
//                    if (t[k].equals(".."))
//                        c++;
//                }
//                StringBuilder res = new StringBuilder("");
//                for (int i = 0; i < strList.length - c; i++) {
//                    res.append("/").append(strList[i]);
//                }
//                if (c != t.length)
//                {
//                    for (int k = 0; k < t.length; k++){
//                        if (!t[k].equals(".."))
//                            res.append("/").append(t[k]);
//                    }
//                }
//                return res.toString();
//            }
//            else if (cond.charAt(0) != '/' && cond.charAt(cond.length() - 1) == '/'){
//                StringBuilder res = new StringBuilder("");
//                for (int i = 0; i < strList.length ; i++) {
//                    res.append("/").append(strList[i]);
//                }
//                return res.append("/").append(cond.substring(0, cond.length() - 1)).toString() ;
//            }
//        }
//        return IllegalStateException.class;
//    }
//}
