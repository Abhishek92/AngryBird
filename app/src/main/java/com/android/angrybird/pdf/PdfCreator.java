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
import com.android.angrybird.util.Utils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by hp pc on 18-06-2017.
 */

public class PdfCreator {

    private String userId;
    private User user;
    private Context mContext;

    private PdfCreator(String userId, User user, Context context) {
        this.userId = userId;
        this.user = user;
        mContext = context;
    }

    public static PdfCreator createPdf(String userId, User user, Context context) {
        return new PdfCreator(userId, user, context);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
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
                    Uri uri = Uri.parse(FILE_NAME);
                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    share.setType("application/pdf");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    mContext.startActivity(share);
                }
            }
        }.execute(userId);
    }

    private void addMetaData(Document document) {
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("AngryBird App");
        document.addCreator("AngryBird App");
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
            pdfPTable.addCell(String.valueOf(item.getAliasNo()));
            pdfPTable.addCell(item.getParticular());
            pdfPTable.addCell(Utils.getFormattedDate(item.getCreatedDate()));
            pdfPTable.addCell(item.getDebitAmount());
            pdfPTable.addCell(item.getCreditAmount());
            pdfPTable.addCell(item.getDebitWeight());
            pdfPTable.addCell(item.getCrediWeight());
            double debitAmt = TextUtils.isEmpty(item.getDebitAmount()) ? 0 : Double.parseDouble(item.getDebitAmount());
            double creditAmt = TextUtils.isEmpty(item.getCreditAmount()) ? 0 : Double.parseDouble(item.getCreditAmount());
            pdfPTable.addCell(String.format("%.2f", debitAmt - creditAmt));
        }

        paragraph.add(pdfPTable);
        document.add(paragraph);
    }

}
