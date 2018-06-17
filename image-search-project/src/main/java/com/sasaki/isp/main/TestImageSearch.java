package com.sasaki.isp.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.ImageDuplicates;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

public class TestImageSearch {  
    
    private static String INDEX_PATH = "/Users/sasaki/git/_/image-search-project/index";// 索引文件存放路径  
    private static String INDEX_FILE_PATH = "/Users/sasaki/vsh/SZU"; //要索引的图片文件目录  
    private static String SEARCH_FILE = "/Users/sasaki/Desktop/refer2.jpeg";//用于搜索的图片  
      
    public void createIndex() throws Exception {  
        //创建一个合适的文件生成器，Lire针对图像的多种属性有不同的生成器  
        DocumentBuilder db = DocumentBuilderFactory.getCEDDDocumentBuilder();  
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_33, new SimpleAnalyzer(Version.LUCENE_33));  
        IndexWriter iw = new IndexWriter(FSDirectory.open(new File(INDEX_PATH)), iwc);  
        File parent = new File(INDEX_FILE_PATH);  
        for (File f : parent.listFiles()) {  
            // 创建Lucene索引  
            Document doc = db.createDocument(new FileInputStream(f), f.getName());  
            // 将文件加入索引  
            iw.addDocument(doc);  
        }  
//        iw.optimize();  
        iw.close();  
    }  
      
    public void searchSimilar() throws Exception {  
        IndexReader ir = IndexReader.open(FSDirectory.open(new File(INDEX_PATH)));//打开索引  
        ImageSearcher is = ImageSearcherFactory.createDefaultSearcher();//创建一个图片搜索器  
        FileInputStream fis = new FileInputStream(SEARCH_FILE);//搜索图片源  
        BufferedImage bi = ImageIO.read(fis);  
        ImageSearchHits ish = is.search(bi, ir);//根据上面提供的图片搜索相似的图片  
        for (int i = 0; i < 10; i++) {//显示前10条记录（根据匹配度排序）  
            System.out.println(ish.score(i) + ": " + ish.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue());  
        }  
    }  
      
    //测试前先将包含重复图片的文件进行索引  
    public void searchDuplicates() throws Exception {  
        IndexReader ir = IndexReader.open(FSDirectory.open(new File(INDEX_PATH)));  
        ImageSearcher is = ImageSearcherFactory.createDefaultSearcher();  
        ImageDuplicates id = is.findDuplicates(ir);//查找重复的图片，如果没有，则返回null  
        for (int i = 0; id != null && i < id.length(); i++) {  
            System.out.println(id.getDuplicate(i).toString());  
        }  
    }  
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
        // TODO Auto-generated method stub  
        TestImageSearch ts = new TestImageSearch();  
        try {  
            ts.createIndex();  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
          
        try {  
            ts.searchSimilar();  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }  
  
}  