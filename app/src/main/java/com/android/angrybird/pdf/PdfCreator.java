package com.android.angrybird.pdf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.Item;
import com.android.angrybird.database.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by hp pc on 18-06-2017.
 */

public class PdfCreator {

    private String userId;
    private User user;
    private Context mContext;
    private Item item;

    private PdfCreator(String userId, User user, Context context) {
        this.userId = userId;
        this.user = user;
        mContext = context;
    }

    private PdfCreator(Item item, Context context) {
        this.item = item;
        mContext = context;
    }

    public static PdfCreator createPdf(String userId, User user, Context context) {
        return new PdfCreator(userId, user, context);
    }

    public static PdfCreator createPdf(Item item, Context context) {
        return new PdfCreator(item, context);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public void createPdfForItems() {
        new AsyncTask<String, Void, String>() {
            private ProgressDialog mDialog;
            private String FILE_NAME = Environment.getExternalStorageDirectory() + "/" + item.getAliasNo() + ".pdf";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mDialog = new ProgressDialog(mContext);
                mDialog.setMessage("Generating Pdf");
                mDialog.setCancelable(false);
                mDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(FILE_NAME));
                    document.open();
                    addMetaData(document);
                    setPdfForItem(item, document);
                    document.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                return "done";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (mDialog.isShowing())
                    mDialog.cancel();
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(mContext, "Error generating pdf..", Toast.LENGTH_SHORT).show();
                } else {
                    sharePdf(FILE_NAME);
                }
            }


        }.execute(userId);
    }

    private void sharePdf(String fileName) {
        File file = new File(fileName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        mContext.startActivity(Intent.createChooser(intent, "Open pdf"));
    }

    public void createAndSharePdf() {
        new AsyncTask<String, Void, String>() {
            private ProgressDialog mDialog;
            private String FILE_NAME = Environment.getExternalStorageDirectory() + "/" + userId + ".pdf";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mDialog = new ProgressDialog(mContext);
                mDialog.setMessage("Generating Pdf");
                mDialog.setCancelable(false);
                mDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                List<Item> itemsList = DBManager.INSTANCE.getDaoSession().getItemDao().queryRaw("WHERE USER_ID = ?", String.valueOf(userId));
                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(FILE_NAME));
                    document.open();
                    addMetaData(document);
                    setHeaderAmountValue(itemsList, document, user);
                    document.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                return "done";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (mDialog.isShowing())
                    mDialog.cancel();
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(mContext, "Error generating pdf..", Toast.LENGTH_SHORT).show();
                } else {
                    sharePdf(FILE_NAME);
                }
            }
        }.execute(userId);
    }

    private void addMetaData(Document document) {
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("AngryBird App");
        document.addCreator("AngryBird App");
    }

    private void setPdfForItem(Item item, Document document) throws DocumentException {
        Paragraph paragraph = new Paragraph();

        PdfPTable pdfPTable = new PdfPTable(8);
        pdfPTable.setWidthPercentage(100);
        PdfPCell c1 = new PdfPCell(new Phrase("S.No"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        c1 = new PdfPCell(new Phrase("Particular"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        c1 = new PdfPCell(new Phrase("Date"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        c1 = new PdfPCell(new Phrase("Debit Amount"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        c1 = new PdfPCell(new Phrase("Credit Amount"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        c1 = new PdfPCell(new Phrase("Debit Weight"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        c1 = new PdfPCell(new Phrase("Credit Weight"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        c1 = new PdfPCell(new Phrase("Balance"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        pdfPTable.setHeaderRows(1);

        addColumn(pdfPTable, String.valueOf(item.getAliasNo()));
        addColumn(pdfPTable, item.getParticular());
        addColumn(pdfPTable, item.getDate());
        addColumn(pdfPTable, item.getDebitAmount());
        addColumn(pdfPTable, item.getCreditAmount());
        addColumn(pdfPTable, item.getDebitWeight());
        addColumn(pdfPTable, item.getCrediWeight());
        double debitAmt = TextUtils.isEmpty(item.getDebitAmount()) ? 0 : Double.parseDouble(item.getDebitAmount());
        double creditAmt = TextUtils.isEmpty(item.getCreditAmount()) ? 0 : Double.parseDouble(item.getCreditAmount());
        addColumn(pdfPTable, String.format("%.2f", debitAmt - creditAmt));

        paragraph.add(pdfPTable);
        document.add(paragraph);
    }

    private void setHeaderAmountValue(List<Item> itemList, Document document, User user) throws DocumentException {

        double totalDebitAmt = 0;
        double totalCreditAmt = 0;
        double totalDebitWeight = 0;
        double totalCeditWeight = 0;
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            double debitAmt = TextUtils.isEmpty(item.getDebitAmount()) ? 0 : Double.parseDouble(item.getDebitAmount());
            double creditAmt = TextUtils.isEmpty(item.getCreditAmount()) ? 0 : Double.parseDouble(item.getCreditAmount());

            double debitWgt = TextUtils.isEmpty(item.getDebitWeight()) ? 0 : Double.parseDouble(item.getDebitWeight());
            double creditWgt = TextUtils.isEmpty(item.getCrediWeight()) ? 0 : Double.parseDouble(item.getCrediWeight());

            totalDebitAmt += debitAmt;
            totalCreditAmt += creditAmt;

            totalDebitWeight += debitWgt;
            totalCeditWeight += creditWgt;
        }
        double balance = totalDebitAmt - totalCreditAmt;
        double wgtbalance = totalDebitWeight - totalCeditWeight;

        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph(String.format("Name: %s %s %s", user.getFirstName(), user.getMiddleName(), user.getLastName())));
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph(String.format("Account No: %s", String.valueOf(user.getAliasNo()))));
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph(String.format("Contact: %s, %s", user.getContactOne(), user.getContactTwo())));
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph(String.format("Total Debit Amount is: %.2f", totalDebitAmt)));
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph(String.format("Total Credit Amount is: %.2f", totalCreditAmt)));
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph(String.format("Amount Balance is: %.2f", balance)));
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph(String.format("Total Debit Weight is: %.2f", totalDebitWeight)));
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph(String.format("Total Credit Weight is: %.2f", totalCeditWeight)));
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph(String.format("Weight Balance is: %.2f", wgtbalance)));

        addEmptyLine(paragraph, 1);

        PdfPTable pdfPTable = new PdfPTable(8);
        pdfPTable.setWidthPercentage(100);
        PdfPCell c1 = new PdfPCell(new Phrase("S.No"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        c1 = new PdfPCell(new Phrase("Particular"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        c1 = new PdfPCell(new Phrase("Date"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        c1 = new PdfPCell(new Phrase("Debit Amount"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        c1 = new PdfPCell(new Phrase("Credit Amount"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        c1 = new PdfPCell(new Phrase("Debit Weight"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        c1 = new PdfPCell(new Phrase("Credit Weight"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        c1 = new PdfPCell(new Phrase("Balance"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(c1);

        pdfPTable.setHeaderRows(1);


        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            addColumn(pdfPTable, String.valueOf(item.getAliasNo()));
            addColumn(pdfPTable, item.getParticular());
            addColumn(pdfPTable, item.getDate());
            addColumn(pdfPTable, item.getDebitAmount());
            addColumn(pdfPTable, item.getCreditAmount());
            addColumn(pdfPTable, item.getDebitWeight());
            addColumn(pdfPTable, item.getCrediWeight());
            double debitAmt = TextUtils.isEmpty(item.getDebitAmount()) ? 0 : Double.parseDouble(item.getDebitAmount());
            double creditAmt = TextUtils.isEmpty(item.getCreditAmount()) ? 0 : Double.parseDouble(item.getCreditAmount());
            addColumn(pdfPTable, String.format("%.2f", debitAmt - creditAmt));
        }

        paragraph.add(pdfPTable);
        document.add(paragraph);
    }

    private void addColumn(PdfPTable pdfPTable, String value) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(value));
        pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPTable.addCell(pdfPCell);
    }

}
