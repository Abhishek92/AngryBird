package com.android.angrybird.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.angrybird.R;
import com.android.angrybird.activity.AddEditUserActivity;
import com.android.angrybird.database.DBManager;
import com.android.angrybird.database.Item;
import com.android.angrybird.database.User;
import com.android.angrybird.databinding.HeaderLayoutViewBinding;
import com.android.angrybird.databinding.UserListItemLayoutBinding;
import com.android.angrybird.fragment.ImageDialogFragment;
import com.android.angrybird.util.Utils;
import com.bumptech.glide.Glide;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.parceler.Parcels;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hp pc on 05-05-2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUserList;
    private List<User> mBackupList;
    private OnItemActionListener mListener;


    public UserListAdapter(Context context, List<User> userList)
    {
        mContext = context;
        mUserList = userList;
        mBackupList = new ArrayList<>(userList);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public void setOnItemClickListener(OnItemActionListener listener)
    {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == R.layout.header_layout_view) {
            HeaderLayoutViewBinding binding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.header_layout_view, parent, false);
            return new ViewHolder(binding);
        } else {
            UserListItemLayoutBinding binding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.user_list_item_layout, parent, false);
            return new ViewHolder(binding);
        }

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final User user = mUserList.get(position);
        if (user.getGender() == null) {
            holder.headerLayoutViewBinding.container.setText(user.getFirstName());
        } else {
            holder.bindData(user);
            holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mListener)
                        mListener.onItemSelected(user);
                }
            });
            Glide.with(mContext).load(user.getUserImagePath()).centerCrop().placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.binding.avatarImg);
            holder.binding.avatarImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageDialogFragment imageDialogFragment = ImageDialogFragment.getInstance(user.getUserImagePath());
                    imageDialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), ImageDialogFragment.TAG);
                }
            });
            holder.binding.title.setText(String.format("%s. %s %s %s", user.getAliasNo(), user.getFirstName(), user.getMiddleName(), user.getLastName()));
            holder.binding.contact.setText("Contact no: ".concat(user.getContactOne()));
            holder.binding.address.setText("Address: ".concat(user.getAddress()));
            setUpPopupMenu(holder, user);
        }
    }

    private void setUpPopupMenu(final ViewHolder holder, final User user) {
        holder.binding.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(mContext, holder.binding.popupMenu);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.edit:
                                Intent intent = new Intent(mContext, AddEditUserActivity.class);
                                intent.putExtra(AddEditUserActivity.KEY_USER_DATA, Parcels.wrap(user));
                                mContext.startActivity(intent);
                                return true;
                            case R.id.delete:
                                new AlertDialog.Builder(mContext).setMessage("Do you want to delete this item?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DBManager.INSTANCE.getDaoSession().getUserDao().deleteByKey(user.getUserId());
                                        if(null != mListener)
                                            mListener.onItemDeleted();
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                }).show();

                                return true;
                            case R.id.share:
                                createAndSharePdf(String.valueOf(user.getUserId()), user);
                                return true;
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });
    }

    private void createAndSharePdf(final String userId, final User user) {
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
        paragraph.add(new Paragraph(String.format("Alias No: %s", String.valueOf(user.getAliasNo()))));
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
            pdfPTable.addCell(item.getCreatedDate());
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

    @Override
    public int getItemViewType(int position) {
        return mUserList.get(position).getGender() == null ? R.layout.header_layout_view : R.layout.user_list_item_layout;
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void flushFilter() {
        mUserList = new ArrayList<>();
        mUserList.addAll(mBackupList);
        notifyDataSetChanged();
    }

    private List<User> getListWithHeader(List<User> list) {
        List<User> userList = new ArrayList<>(list);
        if (Utils.listNotNull(userList)) {
            Collections.sort(userList);
            User headr = new User();
            headr.setFirstName(String.valueOf(userList.get(0).getFirstName().charAt(0)));
            userList.add(0, headr);
            for (int i = 1; i < userList.size(); i++) {
                User user = userList.get(i - 1);
                User nextUser = userList.get(i);
                if (user.getFirstName().charAt(0) != nextUser.getFirstName().charAt(0)) {
                    User headerVal = new User();
                    headerVal.setFirstName(String.valueOf(nextUser.getFirstName().charAt(0)));
                    userList.add(i, headerVal);
                }
            }
        }

        return userList;
    }

    public void setFilter(String queryText) {

        mUserList = new ArrayList<>();
        queryText = queryText.toLowerCase();
        for (User item : mBackupList) {
            String contactOne = TextUtils.isEmpty(item.getContactOne()) ? "" : item.getContactOne();
            String contactTwo = TextUtils.isEmpty(item.getContactTwo()) ? "" : item.getContactTwo();
            String aliasNo = item.getAliasNo() == null ? String.valueOf("0") : String.valueOf(item.getAliasNo());
            if (item.getFirstName().toLowerCase().contains(queryText) || contactOne.toLowerCase().contains(queryText)
                    || contactTwo.toLowerCase().contains(queryText) || aliasNo.toLowerCase().contains(queryText)
                    && item.getGender() != null)
                mUserList.add(item);
        }
        mUserList = getListWithHeader(mUserList);
        notifyDataSetChanged();
    }

    public interface OnItemActionListener {
        void onItemSelected(Object t);

        void onItemDeleted();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private UserListItemLayoutBinding binding;
        private HeaderLayoutViewBinding headerLayoutViewBinding;
        public ViewHolder(UserListItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = DataBindingUtil.getBinding(binding.getRoot());

        }

        public ViewHolder(HeaderLayoutViewBinding binding) {
            super(binding.getRoot());
            this.headerLayoutViewBinding = DataBindingUtil.getBinding(binding.getRoot());
        }

        public void bindData(User organisation) {
            if (null != binding) {
                binding.setData(organisation);
                binding.executePendingBindings();
            }
        }
    }
}
