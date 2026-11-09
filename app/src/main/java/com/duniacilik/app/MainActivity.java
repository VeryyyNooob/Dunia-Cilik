package com.duniacilik.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {

    private final int bg = Color.rgb(255,248,239);
    private final int dark = Color.rgb(65,56,75);
    private final int muted = Color.rgb(128,119,137);

    private String selectedAge = "5–7 Tahun";
    private LinearLayout content;
    private final ArrayList<View> pageStack = new ArrayList<>();

    private static final String UPDATE_MANIFEST_URL =
        "https://raw.githubusercontent.com/VeryyyNooob/Dunia-Cilik/main/update.json";

    private DownloadManager downloadManager;
    private long updateDownloadId = -1;
    private BroadcastReceiver updateDownloadReceiver;
    private boolean updateReceiverRegistered = false;


    private final String[][] modules = {
        {"🔤","Bahasa Melayu","ABC, membaca dan mengeja","#FFE1A8"},
        {"✏️","Mengeja","Kenal huruf dan bina perkataan","#FFD9B8"},
        {"📖","Mengaji Iqra","Belajar huruf hijaiyah dan bacaan","#DCD5FF"},
        {"🔢","Matematik","Nombor dan kira-kira","#D8E8FF"},
        {"🎨","Mewarna","Warna dan hasilkan lukisan","#FFD9E8"},
        {"🧪","Sains","Kenali alam dan dunia","#D5F3DD"},
        {"🧩","Permainan Minda","Puzzle dan teka-teki","#E7DDFF"},
        {"🌍","Kenali Dunia","Haiwan, tumbuhan dan objek","#D7F1F1"}
    };

    private final String[] latin = {
        "A","B","C","D","E","F","G","H","I","J","K","L","M",
        "N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
    };

    private final String[] hijaiyah = {
        "ا","ب","ت","ث","ج","ح","خ","د","ذ","ر","ز",
        "س","ش","ص","ض","ط","ظ","ع","غ","ف","ق","ك",
        "ل","م","ن","و","ه","ء","ي"
    };

    private final String[] hijaiyahNames = {
        "Alif","Ba","Ta","Tsa","Jim","Ha","Kho","Dal","Dzal",
        "Ra","Zai","Sin","Syin","Sad","Dad","To","Zo","Ain",
        "Ghain","Fa","Qaf","Kaf","Lam","Mim","Nun","Wau",
        "Ha","Hamzah","Ya"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        buildHome();
    }

    private void showPage(View page, boolean replaceCurrent) {
        if (replaceCurrent && !pageStack.isEmpty()) {
            pageStack.set(pageStack.size() - 1, page);
        } else {
            pageStack.add(page);
        }
        setContentView(page);
    }

    private void handleBackFlow() {
        if (pageStack.size() > 1) {
            pageStack.remove(pageStack.size() - 1);
            setContentView(pageStack.get(pageStack.size() - 1));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        handleBackFlow();
    }

    private int dp(float value) {
        return (int)(value * getResources()
            .getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private TextView text(String value, int size, int color,
                          boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        if (bold) t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private void space(LinearLayout parent, int height) {
        View v = new View(this);
        parent.addView(v,new LinearLayout.LayoutParams(1,dp(height)));
    }

    private void buildHome() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(bg);

        LinearLayout root = column();
        root.setPadding(dp(20),dp(18),dp(20),dp(28));
        scroll.addView(root);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout brand = column();
        brand.addView(text("DUNIA CILIK",12,
            Color.rgb(255,135,105),true));
        brand.addView(text("Hai, Adik! 👋",26,dark,true));

        header.addView(brand,
            new LinearLayout.LayoutParams(0,-2,1));

        TextView profile = text("👨‍👩‍👧 Profil",13,dark,true);
        profile.setPadding(dp(13),dp(11),dp(13),dp(11));
        profile.setBackground(shape(Color.WHITE,18));
        profile.setOnClickListener(v -> showParent());
        header.addView(profile);
        root.addView(header);

        space(root,20);

        LinearLayout hero = column();
        hero.setPadding(dp(20),dp(20),dp(20),dp(20));
        hero.setBackground(shape(Color.rgb(255,220,174),25));
        hero.setElevation(dp(5));
        hero.addView(text("🌈 SELAMAT DATANG!",12,dark,true));
        space(hero,8);
        hero.addView(text("Belajar Jadi Seronok!",23,dark,true));
        space(hero,5);
        hero.addView(text(
            "Jom belajar, bermain dan cipta sesuatu yang hebat hari ini.",
            14,dark,false));
        root.addView(hero);

        space(root,23);
        root.addView(text("Pilih peringkat umur",18,dark,true));
        space(root,10);

        LinearLayout ages = new LinearLayout(this);
        String[] ageList = {
            "2–4 Tahun","5–7 Tahun","8–10 Tahun","11–12 Tahun"
        };

        for (String age : ageList) {
            TextView chip = text(age,10,dark,true);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(dp(7),dp(12),dp(7),dp(12));
            chip.setBackground(shape(
                age.equals(selectedAge)
                    ? Color.rgb(255,213,157) : Color.WHITE,14));

            LinearLayout.LayoutParams cp =
                new LinearLayout.LayoutParams(0,-2,1);
            cp.setMargins(dp(2),0,dp(2),0);
            ages.addView(chip,cp);

            chip.setOnClickListener(v -> {
                selectedAge = age;
                buildHome();
            });
        }

        root.addView(ages);
        space(root,24);
        root.addView(text("Apa yang nak belajar? 📚",19,dark,true));
        space(root,13);

        for (int i=0;i<modules.length;i+=2) {
            LinearLayout row = new LinearLayout(this);
            addModule(row,modules[i]);
            if (i+1<modules.length) addModule(row,modules[i+1]);
            root.addView(row);
            space(root,12);
        }

        space(root,10);
        TextView parent = text(
            "👨‍👩‍👧 Dashboard Ibu Bapa",16,dark,true);
        parent.setGravity(Gravity.CENTER);
        parent.setPadding(dp(15),dp(17),dp(15),dp(17));
        parent.setBackground(shape(Color.WHITE,20));
        parent.setOnClickListener(v -> showParent());
        root.addView(parent);

        space(root,18);
        TextView footer = text(
            "Dunia Cilik • Belajar Dengan Gembira 💛",
            11,muted,false);
        footer.setGravity(Gravity.CENTER);
        root.addView(footer);

        showPage(scroll, true);
    }

    private void addModule(LinearLayout row,String[] module) {
        LinearLayout card = column();
        card.setPadding(dp(14),dp(15),dp(12),dp(14));
        card.setBackground(shape(Color.parseColor(module[3]),22));
        card.setElevation(dp(4));

        card.addView(text(module[0],32,dark,true));
        space(card,8);
        card.addView(text(module[1],15,dark,true));
        space(card,5);

        TextView desc = text(module[2],11,dark,false);
        desc.setMinHeight(dp(34));
        card.addView(desc);

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(0,dp(158),1);
        p.setMargins(dp(4),dp(3),dp(4),dp(3));
        row.addView(card,p);
        card.setOnClickListener(v -> showModule(module));
    }

    private void showModule(String[] module) {
        if (module[1].equals("Mengeja")) {
            showSpelling();
        } else if (module[1].equals("Mengaji Iqra")) {
            showIqraLevels();
        } else {
            android.app.AlertDialog.Builder b =
                new android.app.AlertDialog.Builder(this);
            b.setTitle(module[0]+" "+module[1]);
            b.setMessage("Peringkat umur: "+selectedAge+
                "\n\n"+module[2]+
                "\n\nModul ini akan dibangunkan dalam fasa seterusnya.");
            b.setPositiveButton("Baik",(d,w)->d.dismiss());
            b.show();
        }
    }

    private void prepareLearningPage(String title,String subtitle) {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(bg);
        scroll.setFillViewport(true);

        content = column();
        content.setPadding(dp(20),dp(18),dp(20),dp(30));
        scroll.addView(content);

        TextView back = text("← Kembali",15,dark,true);
        back.setPadding(dp(12),dp(12),dp(12),dp(12));
        back.setBackground(shape(Color.WHITE,15));
        back.setOnClickListener(v -> handleBackFlow());
        content.addView(back);

        space(content,20);
        content.addView(text(title,25,dark,true));
        space(content,6);
        content.addView(text(subtitle,14,muted,false));
        space(content,10);
        content.addView(text("Umur dipilih: "+selectedAge,13,dark,true));
        space(content,20);

        showPage(scroll, false);
    }

    private int ageGroup() {
        if (selectedAge.startsWith("2")) return 0;
        if (selectedAge.startsWith("5")) return 1;
        if (selectedAge.startsWith("8")) return 2;
        return 3;
    }

    private void showSpelling() {
        prepareLearningPage("✏️ Jom Mengeja",
            "Belajar huruf, bunyi dan perkataan Bahasa Melayu.");

        int group = ageGroup();

        if (group==0) {
            content.addView(text("Kenali huruf A hingga Z",19,dark,true));
            space(content,8);
            content.addView(text(
                "Tekan huruf untuk kenal bentuk dan contoh perkataan.",
                14,muted,false));
            space(content,15);

            String[] examples = {
                "Ayam","Bola","Cawan","Dadu","Emak","Feri",
                "Gajah","Harimau","Ikan","Jagung","Kasut",
                "Lampu","Mata","Nasi","Oren","Pensel",
                "Quran","Rumah","Susu","Topi","Ular",
                "Van","Wau","Xilofon","Yoyo","Zebra"
            };

            for (int i=0;i<latin.length;i+=2) {
                LinearLayout row = new LinearLayout(this);
                addLetterCard(row,latin[i],examples[i]);
                if (i+1<latin.length)
                    addLetterCard(row,latin[i+1],examples[i+1]);
                content.addView(row);
                space(content,8);
            }

            addSpellingQuiz();
        } else {
            String[][] words = {
                {"BA","BI","BU","BE","BO"},
                {"BOLA","BUKU","SUSU","MATA","KAKI"},
                {"SEKOLAH","KELUARGA","BUNGA","KUCING","PANTAI"}
            };

            String heading = group==1
                ? "Kenal suku kata"
                : group==2 ? "Baca dan bina perkataan"
                : "Latihan ejaan lanjutan";

            content.addView(text(heading,19,dark,true));
            space(content,12);

            String[] selected = words[group==1?0:group==2?1:2];
            for (String word:selected) {
                TextView item = text("🔊   "+word,22,dark,true);
                item.setPadding(dp(16),dp(18),dp(16),dp(18));
                item.setBackground(shape(Color.WHITE,18));
                content.addView(item);
                space(content,9);
            }
            addSpellingQuiz();
        }
    }

    private void addLetterCard(LinearLayout row,String letter,
                               String example) {
        TextView card = text(letter+"  "+letter.toLowerCase()+
            "\n"+example,20,dark,true);
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(8),dp(14),dp(8),dp(14));
        card.setBackground(shape(Color.WHITE,17));

        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(0,-2,1);
        p.setMargins(dp(3),0,dp(3),0);
        row.addView(card,p);

        card.setOnClickListener(v ->
            Toast.makeText(this,
                letter+" untuk "+example,
                Toast.LENGTH_SHORT).show());
    }

    private void addSpellingQuiz() {
        space(content,20);
        content.addView(text("🎯 Uji minda: Cari huruf!",19,dark,true));
        space(content,10);

        final String[] target = {"A","B","C","D","M","S","K","Z"};
        final int[] index = {0};

        TextView question = text("",24,dark,true);
        question.setGravity(Gravity.CENTER);
        question.setPadding(dp(12),dp(18),dp(12),dp(18));
        question.setBackground(shape(Color.rgb(255,230,190),18));
        content.addView(question);

        LinearLayout choices = new LinearLayout(this);
        choices.setGravity(Gravity.CENTER);
        content.addView(choices);

        TextView feedback = text("Pilih jawapan yang betul.",14,muted,false);
        content.addView(feedback);

        Runnable[] render = new Runnable[1];
        render[0] = () -> {
            String answer = target[index[0]%target.length];
            question.setText("Cari huruf  "+answer);
            choices.removeAllViews();

            List<String> options = new ArrayList<>();
            options.add(answer);
            for (String s:latin) {
                if (options.size()<4 && !options.contains(s))
                    options.add(s);
            }
            Collections.shuffle(options);

            for (String option:options) {
                Button button = new Button(this);
                button.setText(option);
                button.setTextSize(22);
                LinearLayout.LayoutParams p =
                    new LinearLayout.LayoutParams(0,dp(64),1);
                p.setMargins(dp(3),dp(8),dp(3),dp(8));
                choices.addView(button,p);

                button.setOnClickListener(v -> {
                    if (option.equals(answer)) {
                        feedback.setText("Bagus! Betul! ⭐");
                        index[0]++;
                        render[0].run();
                    } else {
                        feedback.setText("Cuba lagi. Cari huruf "+answer+".");
                    }
                });
            }
        };
        render[0].run();
    }

    private void showIqraLevels() {
        prepareLearningPage("📖 Mengaji Iqra",
            "Belajar huruf hijaiyah secara berperingkat.");

        content.addView(text(
            "Pilih tahap latihan",19,dark,true));
        space(content,12);

        String[] descriptions = {
            "Kenal huruf hijaiyah satu persatu",
            "Kenal bentuk dan gabungan huruf",
            "Bacaan dengan baris atas (fathah)",
            "Bacaan baris bawah dan depan",
            "Latihan tanwin dan sukun",
            "Latihan gabungan dan bacaan lebih lanjut"
        };

        for (int i=0;i<6;i++) {
            final int level=i+1;
            LinearLayout card=column();
            card.setPadding(dp(16),dp(15),dp(16),dp(15));
            card.setBackground(shape(
                i%2==0?Color.rgb(229,221,255):Color.WHITE,18));

            card.addView(text("IQRA "+level,19,dark,true));
            space(card,5);
            card.addView(text(descriptions[i],13,dark,false));

            LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1,-2);
            p.setMargins(0,0,0,dp(10));
            content.addView(card,p);
            card.setOnClickListener(v -> showIqraLesson(level));
        }

        space(content,10);
        content.addView(text(
            "Nota: Latihan ini ialah pengenalan digital. " +
            "Semakan guru atau ibu bapa tetap penting untuk " +
            "memastikan makhraj dan sebutan betul.",
            12,muted,false));
    }

    private void showIqraLesson(int level) {
        prepareLearningPage("📖 Iqra "+level,
            "Baca perlahan-lahan dan kenal bentuk huruf.");

        if (level==1) {
            content.addView(text(
                "Huruf hijaiyah asas",19,dark,true));
            space(content,10);

            for (int i=0;i<hijaiyah.length;i+=3) {
                LinearLayout row=new LinearLayout(this);
                for (int j=i;j<Math.min(i+3,hijaiyah.length);j++) {
                    final int k=j;
                    TextView card=text(
                        hijaiyah[j]+"\n"+hijaiyahNames[j],
                        22,dark,true);
                    card.setGravity(Gravity.CENTER);
                    card.setPadding(dp(5),dp(15),dp(5),dp(15));
                    card.setBackground(shape(Color.WHITE,16));

                    LinearLayout.LayoutParams p =
                        new LinearLayout.LayoutParams(0,dp(90),1);
                    p.setMargins(dp(3),dp(3),dp(3),dp(3));
                    row.addView(card,p);
                    card.setOnClickListener(v ->
                        Toast.makeText(this,
                            hijaiyahNames[k],
                            Toast.LENGTH_SHORT).show());
                }
                content.addView(row);
            }
        } else {
            String[][] samples = {
                {},
                {"بَ","تَ","ثَ","جَ","حَ","خَ"},
                {"بَ","تَ","جَ","دَ","رَ","سَ"},
                {"بِ","تِ","جِ","بُ","تُ","جُ"},
                {"بً","بٍ","بٌ","مْ","نْ","لْ"},
                {"بَا","بِي","بُو","مَا","مِي","مُو"}
            };

            String explanation;
            switch(level) {
                case 2:
                    explanation="Perhatikan bentuk huruf dan sambungannya.";
                    break;
                case 3:
                    explanation="Baris atas menghasilkan bunyi a.";
                    break;
                case 4:
                    explanation="Baris bawah berbunyi i, baris depan berbunyi u.";
                    break;
                case 5:
                    explanation="Pengenalan tanwin dan tanda sukun.";
                    break;
                default:
                    explanation="Pengenalan bacaan panjang asas.";
                    break;
            }

            content.addView(text(explanation,15,dark,true));
            space(content,15);

            for (String symbol:samples[level-1]) {
                TextView card=text(symbol,36,dark,true);
                card.setGravity(Gravity.CENTER);
                card.setPadding(dp(12),dp(13),dp(12),dp(13));
                card.setBackground(shape(Color.WHITE,16));
                content.addView(card);
                space(content,8);
            }
        }

        space(content,18);
        content.addView(text(
            "Latihan bersama ibu bapa atau guru",
            16,dark,true));
        space(content,8);

        content.addView(text(
            "Dengar contoh bacaan daripada guru, kemudian " +
            "sebut semula. Audio bacaan yang disahkan akan " +
            "ditambah dalam fasa audio.",
            13,muted,false));
    }

    private void showParent() {
        new android.app.AlertDialog.Builder(this)
            .setTitle("Dashboard Ibu Bapa")
            .setMessage(
                "Di sini ibu bapa akan dapat:\n\n"+
                "• Mengurus profil anak\n"+
                "• Melihat kemajuan pembelajaran\n"+
                "• Memantau bintang dan ganjaran\n"+
                "• Mengurus tetapan audio\n"+
                "• Mengawal kandungan mengikut umur\n\n"+
                "Dashboard lengkap akan dibangunkan dalam fasa seterusnya."
            )
            .setNeutralButton("Semak Kemas Kini",(d,w)->{
                d.dismiss();
                checkForUpdates();
            })
            .setPositiveButton("Baik",(d,w)->d.dismiss())
            .show();
    }

    private void checkForUpdates() {
        Toast.makeText(this,"Sedang menyemak kemas kini...",
            Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(UPDATE_MANIFEST_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                connection.setInstanceFollowRedirects(true);

                int response = connection.getResponseCode();
                if (response != HttpURLConnection.HTTP_OK) {
                    throw new Exception("HTTP " + response);
                }

                StringBuilder body = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                        connection.getInputStream(),
                        java.nio.charset.StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        body.append(line);
                    }
                }

                JSONObject json = new JSONObject(body.toString());
                int remoteCode = json.getInt("versionCode");
                String remoteName = json.getString("versionName");
                String apkUrl = json.getString("apkUrl");
                String notes = json.optString(
                    "releaseNotes","Penambahbaikan aplikasi.");

                if (!apkUrl.startsWith("https://")) {
                    throw new Exception("Pautan APK bukan HTTPS");
                }

                PackageInfo info = getPackageManager()
                    .getPackageInfo(getPackageName(),0);
                int currentCode = info.versionCode;

                runOnUiThread(() -> {
                    if (remoteCode > currentCode) {
                        showUpdateAvailable(
                            remoteName,remoteCode,apkUrl,notes);
                    } else {
                        Toast.makeText(this,
                            "Dunia Cilik sudah versi terkini.",
                            Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                String error = e.getMessage() == null
                    ? "Ralat tidak diketahui" : e.getMessage();
                runOnUiThread(() -> Toast.makeText(this,
                    "Tidak dapat menyemak kemas kini: " + error,
                    Toast.LENGTH_LONG).show());
            } finally {
                if (connection != null) connection.disconnect();
            }
        }).start();
    }

    private void showUpdateAvailable(
        String versionName,int versionCode,
        String apkUrl,String notes) {

        new android.app.AlertDialog.Builder(this)
            .setTitle("Kemas Kini Dunia Cilik")
            .setMessage(
                "Versi baharu: " + versionName +
                " (build " + versionCode + ")\n\n" +
                notes + "\n\n" +
                "Muat turun APK untuk memasang versi baharu?"
            )
            .setNegativeButton("Nanti", (d,w)->d.dismiss())
            .setPositiveButton("Muat Turun", (d,w)->{
                d.dismiss();
                downloadUpdate(apkUrl,versionName);
            })
            .show();
    }

    private void downloadUpdate(String apkUrl,String versionName) {
        if (Build.VERSION.SDK_INT >= 26 &&
            !getPackageManager().canRequestPackageInstalls()) {

            new android.app.AlertDialog.Builder(this)
                .setTitle("Benarkan Pemasangan")
                .setMessage(
                    "Benarkan Dunia Cilik memasang kemas kini. " +
                    "Aktifkan pilihan Benarkan daripada sumber ini, " +
                    "kemudian kembali ke aplikasi."
                )
                .setNegativeButton("Batal",(d,w)->d.dismiss())
                .setPositiveButton("Buka Tetapan",(d,w)->{
                    Intent settings = new Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:" + getPackageName()));
                    startActivity(settings);
                })
                .show();
            return;
        }

        try {
            downloadManager = (DownloadManager)
                getSystemService(Context.DOWNLOAD_SERVICE);

            if (downloadManager == null) {
                throw new Exception("DownloadManager tidak tersedia");
            }

            registerUpdateReceiver();

            DownloadManager.Request request =
                new DownloadManager.Request(Uri.parse(apkUrl));

            request.setTitle("Dunia Cilik " + versionName);
            request.setDescription("Memuat turun kemas kini...");
            request.setMimeType(
                "application/vnd.android.package-archive");
            request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(false);
            request.setDestinationInExternalFilesDir(
                this,Environment.DIRECTORY_DOWNLOADS,
                "Dunia-Cilik-update.apk");

            updateDownloadId = downloadManager.enqueue(request);

            Toast.makeText(this,
                "Muat turun bermula. Tunggu sehingga selesai.",
                Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this,
                "Gagal memulakan muat turun: " + e.getMessage(),
                Toast.LENGTH_LONG).show();
        }
    }

    private void registerUpdateReceiver() {
        if (updateReceiverRegistered) return;

        updateDownloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context,Intent intent) {
                if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE
                    .equals(intent.getAction())) return;

                long completedId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID,-1);

                if (completedId != updateDownloadId) return;

                DownloadManager.Query query =
                    new DownloadManager.Query().setFilterById(
                        updateDownloadId);

                try (Cursor cursor = downloadManager.query(query)) {
                    if (cursor == null || !cursor.moveToFirst()) {
                        throw new Exception("Status muat turun tidak ditemui");
                    }

                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(
                        DownloadManager.COLUMN_STATUS));

                    if (status != DownloadManager.STATUS_SUCCESSFUL) {
                        int reason = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                DownloadManager.COLUMN_REASON));
                        throw new Exception("Muat turun gagal (" + reason + ")");
                    }

                    Uri apkUri = downloadManager.getUriForDownloadedFile(
                        updateDownloadId);

                    if (apkUri == null) {
                        throw new Exception("Fail APK tidak ditemui");
                    }

                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(
                        apkUri,"application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(install);

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,
                        "Tidak dapat membuka pemasang: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                }
            }
        };

        IntentFilter filter = new IntentFilter(
            DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(updateDownloadReceiver,filter,
                Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(updateDownloadReceiver,filter);
        }

        updateReceiverRegistered = true;
    }

    @Override
    protected void onDestroy() {
        if (updateReceiverRegistered &&
            updateDownloadReceiver != null) {
            try {
                unregisterReceiver(updateDownloadReceiver);
            } catch (Exception ignored) {
            }
            updateReceiverRegistered = false;
        }
        super.onDestroy();
    }
}
