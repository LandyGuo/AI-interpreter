package com.alipay.aiml.utils;

import org.w3c.dom.Node; 

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;
import java.io.BufferedReader;  
import java.io.InputStreamReader; 
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.aiml.App;

/**
 * Created by mujian on 21/10/16.
 *
 * @author mujian
 */
public class AppUtils {
    private static Random random = new Random();
    
    private static final Logger LOG = LoggerFactory.getLogger(AppUtils.class);
    
    public static <E> E getRandom(List<E> collection) {
        return collection.get(random.nextInt(collection.size()));
    }

    /*
     * 把一个DOM node节点(树形结构)反向转化为xml文本，并通过去除文本中的标签，获取标签内部的内容
     * 
     */
    public static String node2String(Node node) {
        String nodeName = node.getNodeName();
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        
        LOG.debug("transform dom to xml:"+sw);
        //去除xml文本的标签
        return sw.toString().replaceAll("(\r\n|\n\r|\r|\n)", " ").replaceAll("> ", ">")
                .replaceFirst("<" + nodeName + ">", "").replaceFirst("</" + nodeName + ">", "");
    }
    
    /**
     * 调用系统命令
     */
    public static String exeCmd(String commandStr) {  
        BufferedReader br = null;  
        try {  
            Process p = Runtime.getRuntime().exec(commandStr);  
            br = new BufferedReader(new InputStreamReader(p.getInputStream(),"GBK"));  
            String line = null;  
            StringBuilder sb = new StringBuilder();  
            while ((line = br.readLine()) != null) {  
                sb.append(line + "\n");  
            }
//            String xmString = new String(sb.toString().getBytes("GBK")); 
            String str = new String(sb.toString().getBytes(),"UTF-8");
            String xmlUTF8 = URLEncoder.encode(str, "GBK"); 
            return str;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }   
        finally  
        {  
            if (br != null)  
            {  
                try {  
                    br.close();  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }
		return "";  
    }  
    
    
    
  public static byte[] getUTF8BytesFromGBKString(String gbkStr) {  
      int n = gbkStr.length();  
      byte[] utfBytes = new byte[3 * n];  
      int k = 0;  
      for (int i = 0; i < n; i++) {  
          int m = gbkStr.charAt(i);  
          if (m < 128 && m >= 0) {  
              utfBytes[k++] = (byte) m;  
              continue;  
          }  
          utfBytes[k++] = (byte) (0xe0 | (m >> 12));  
          utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));  
          utfBytes[k++] = (byte) (0x80 | (m & 0x3f));  
      }  
      if (k < utfBytes.length) {  
          byte[] tmp = new byte[k];  
          System.arraycopy(utfBytes, 0, tmp, 0, k);  
          return tmp;  
      }  
      return utfBytes;  
  }
    
    
}
