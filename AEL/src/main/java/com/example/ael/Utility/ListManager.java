package com.example.ael.Utility;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListManager {
    //todo maybe include an 'included orders' subheading at the top of each?
    public static String createLists(String[] refNos) {
        String ref = genPath();
        createPickList(refNos, ref);
        createPackList(refNos, ref);
        return ref;
    }

    private static void createPackList(String[] refNos, String listRef) {
        float currentPos = 700;
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        PDPageContentStream contentStream = null;
        PDFont headingFont = null;
        try {
            contentStream = new PDPageContentStream(doc, page);
            headingFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            contentStream.beginText();
            contentStream.setFont(headingFont, 20);
            contentStream.newLineAtOffset(30, currentPos);
            currentPos -= 30;
            contentStream.showText("Pack List #" +listRef);
            contentStream.endText();
        } catch (IOException e) { return; }

        for (String refNo: refNos) {
            String query = "SELECT OrderItems.SKU, OrderItems.Quantity, Products.ProductID, Products.Title FROM OrderItems INNER JOIN ProductSKU ON OrderItems.SKU=ProductSKU.SKU INNER JOIN Products ON ProductSKU.ProductID=Products.ProductID WHERE ReferenceNum LIKE '"+refNo+"';";

            String[][] content = {{"SKU","Quantity","ProductID","Title"}};
            Connection connection = DatabaseManager.connect();

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String[][] data = {
                                {
                                        resultSet.getString("SKU"),
                                        resultSet.getString("Quantity"),
                                        resultSet.getString("ProductID"),
                                        resultSet.getString("Title")
                                }
                        };
                        content = append(content,data);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DatabaseManager.closeConnection(connection);
            }

            try{
                contentStream.beginText();
                contentStream.setFont(headingFont, 12);
                contentStream.newLineAtOffset(30, currentPos); //todo prolly store y offset as a method-scope variable and increase when things are drawn
                currentPos -= 10;
                contentStream.showText("Order #"+refNo);
                contentStream.endText();
                currentPos = drawTable(page, contentStream,currentPos,30,content);
            } catch (IOException ex) {}
        }

        try {
            contentStream.close();
            doc.save(("./orderLists/"+listRef+"_Pack.pdf"));
            doc.close();
        } catch (IOException e ) {}
    }

    private static void createPickList(String[] refNos, String listRef) {
        String refNoLine = "(";
        for (int i = 0; i < refNos.length; i++) {
            refNoLine += ("'"+refNos[i]+"'");
            if (!(i==refNos.length-1)) {
                refNoLine+=",";
            }
        }
        refNoLine += ")";

        String query = "SELECT SKUTotals.SKU, SKUTotals.Total, ProductSKU.WarehouseLocation, Products.Title\n" +
                "FROM (\n" +
                "\tSELECT OrderSKUs.SKU, SUM(Quantity) AS Total\n" +
                "\tFROM (\n" +
                "\t\tSELECT SKU, Quantity FROM OrderItems WHERE ReferenceNum IN "+refNoLine+"\n" +
                "\t) AS OrderSKUs\n" +
                "\tGROUP BY OrderSKUs.SKU\n" +
                ") AS SKUTotals\n" +
                "INNER JOIN ProductSKU ON ProductSKU.SKU=SKUTotals.SKU\n" +
                "INNER JOIN Products ON ProductSKU.ProductID=Products.ProductID;";

        String[][] content = {{"SKU","Total","WarehouseLocation","Title"}};
        Connection connection = DatabaseManager.connect();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String[][] data = {
                            {
                                resultSet.getString("SKU"),
                                resultSet.getString("Total"),
                                resultSet.getString("WarehouseLocation"),
                                resultSet.getString("Title")
                            }
                    };
                    content = append(content,data);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }

        try{
            PDDocument doc = new PDDocument();
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(doc,page);
            PDFont headingFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            contentStream.beginText();
            contentStream.setFont(headingFont,20);
            contentStream.newLineAtOffset(30, 700);
            contentStream.showText( "Pick List #"+listRef);
            contentStream.endText();

            drawTable(page, contentStream,690,30,content);
            contentStream.close();

            doc.save(("./orderLists/"+listRef+"_Pick.pdf"));
            doc.close();
        } catch (IOException ex) {}
    }

    private static String[][] append(String[][] a, String[][] b){
        String[][] result = new String[a.length + b.length][];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private static float drawTable(PDPage page,PDPageContentStream contentStream,float y,float margin,String[][] content)throws IOException{
        PDFont colFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDFont itemFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);
        final int rows = content.length;
        final int cols = content[0].length;
        final float rowHeight = 20.0f;
        final float tableWidth = page.getMediaBox().getWidth()-(2.0f*margin);
        final float tableHeight = rowHeight*(float)rows;
        final float colWidth = tableWidth/(float)cols;
        final float cellMargin = 2f;
        //draw rows
        float nexty = y;
        for(int i=0;i<=rows;i++){
            contentStream.moveTo(margin,nexty);
            contentStream.lineTo(margin+tableWidth,nexty);
            contentStream.stroke();
            nexty -= rowHeight;
        }
        //draw cols
        float nextx = margin;
        for(int i=0;i<=cols;i++){
            contentStream.moveTo(nextx,y);
            contentStream.lineTo(nextx,y-tableHeight);
            contentStream.stroke();
            nextx += colWidth;
        }
        //Add strings
        float textx = margin + cellMargin;
        float texty = y - 15.0f;
        for(int i = 0; i < content.length; i++) {
            if(i == 0) {
                contentStream.setFont(colFont,8);
            }
            else {
                contentStream.setFont(itemFont,6);
            }
            for(String text : content[i]){
                contentStream.beginText();
                contentStream.newLineAtOffset(textx, texty);
                contentStream.showText(text);
                contentStream.endText();
                textx += colWidth;
            }
            texty -= rowHeight;
            textx = margin + cellMargin;
        }

        return nexty;
    }

    private static String genPath() {
        File listDir = new File("./orderLists");
        listDir.mkdirs();
        File[] existingLists = listDir.listFiles();

        Integer highestNum = 0;

        for (int i = 0; i < existingLists.length; i++) {
            if (existingLists[i].isFile()) {
                String fileName = existingLists[i].getName();
                String fileNum = fileName.substring(fileName.length()-8, fileName.length()-4);
                Integer currentNum = 0;
                try {
                    currentNum = Integer.parseInt(fileNum);
                    if(currentNum > highestNum) {
                        highestNum = currentNum;
                    }
                }
                catch (NumberFormatException e) {
                    //skips this file
                }
            }
        }
        return String.format("%08d", highestNum+1);
    }
}
