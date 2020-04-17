package julius.todolist.com.dstv.view;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import julius.todolist.com.dstv.R;
import julius.todolist.com.dstv.database.DatabaseHelper;
import julius.todolist.com.dstv.database.DatabaseHelperb;
import julius.todolist.com.dstv.database.model.Note;
import julius.todolist.com.dstv.database.model.Noted;
import julius.todolist.com.dstv.utils.RecyclerTouchListener;
import julius.todolist.com.dstv.utils.Session;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Julius Makoko on 01/04/20.
 */

public class CompletedActivity extends Fragment {
    private CompletedActivity.NotesAdaptered mAdapter;
    private CompletedActivity.NotesAdapter mAdapterCount;
   // private CompletedActivity.NotesAdaptered mAdapterCount;
  //  private TaskActivity.NotesAdapter mAdapterCount;
    private List<Noted> notesList = new ArrayList<>();
    private List<Note> notesLists = new ArrayList<>();
    private DatabaseHelperb db;
    private DatabaseHelperb dbs;
    private AdView mAdView;
    private int progressStatus = 0;
    private ProgressBar pbProgressBar;
    private Handler handler = new Handler();
    private DatabaseHelper dbss;
    private boolean useDarkTheme ;
    private View v ;
    private TextView noNotesView;
    private TextView noNotesView2;
    private  TextView count;
    private Session session;
    private RecyclerView recyclerView;
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_completed, container, false);
        noNotesView = v.findViewById(R.id.textV);
        noNotesView2 = v.findViewById(R.id.textV2);


        SharedPreferences preferences = this.getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        mAdapterCount = new CompletedActivity.NotesAdapter(getActivity(), notesLists);

        useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, true);
        dbs = new DatabaseHelperb(getActivity());
        notesList.addAll(dbs.getAllNotes());
        dbss = new DatabaseHelper(getActivity());
        notesLists.addAll(dbss.getAllData());

        ProgressThread();
        if(useDarkTheme) {
            TextView textView = v.findViewById(R.id.textViewCompleted);
            textView.setTextColor(Color.BLACK);
            TextView tView = v.findViewById(R.id.textView);
            tView.setTextColor(Color.BLACK);
            TextView text = v.findViewById(R.id.tvCompleted);
            text.setTextColor(Color.BLACK);
            noNotesView = v.findViewById(R.id.textV);
            noNotesView2 = v.findViewById(R.id.textV2);
            noNotesView.setTextColor(Color.BLACK);
            pbProgressBar = v.findViewById(R.id.progressBar);
            RelativeLayout l = v.findViewById(R.id.main_layout);
            l.setBackgroundColor(Color.parseColor("#ffffff"));
        }


        recyclerView = v.findViewById(R.id.lil);
        mAdapter = new CompletedActivity.NotesAdaptered(getActivity(), notesList);
        mAdapterCount = new CompletedActivity.NotesAdapter(getActivity(), notesLists);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        someMethod();
       // ProgressThread();

        toggleEmptyNotes();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view,final int position) {
                final Noted noted = notesList.get(position);
                String x = noted.getTimestamped();
                String y = noted.getTimed();
                String z = noted.getNoted();
                createNoteds(z,y,x);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deleteNoted(position);
                        toggleEmptyNotes();
                        someMethod();
                      //  ProgressThread();
                    }
                }, 950); }
            @Override
            public void onLongClick(View view, int position) {}
        }));
        return v;
            }

    private void ProgressThread() {
        // Start long running operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            int complete = mAdapterCount.getItemCount();
                            int prog = mAdapter.getItemCount();
                            //checks if all open tasks are closed and updates the progress bar
                            if(complete == 0 && prog > 0){
                                pbProgressBar.setProgress(100);
                            }else{
                                pbProgressBar.setProgress(prog);
                            }


                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private  void createNoted(String noted, String timestamped, String timed) {
        long idb = db.insertNoted(noted,timestamped,timed);
        Noted n = db.getNoteb(idb);
        if (n != null) {
            notesList.add(0, n);
            mAdapter.notifyDataSetChanged();
        }}
    private  void createNoteds(String noted, String timestamped, String timed) {
        long id = dbss.insertNote(noted,timestamped,timed);
        Note n = dbss.getNote(id); }
    private void toggleEmptyNotes() {
        if (dbs.getNotesCountb() > 0) {
            noNotesView.setVisibility(View.GONE);
            noNotesView2.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
            noNotesView2.setVisibility(View.VISIBLE);
        }}
    private void updateNoted(String noted, String timed , String timestamped,  int position) {
        Noted n = notesList.get(position);
        n.setNoted(noted);
        n.setTimed(timed);
        n.setTimestamped(timestamped);
        db.updateNoteb(n);
        notesList.set(position, n);
        mAdapter.notifyItemChanged(position);
    }
    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    void someMethod() {
        Runnable task = new Runnable() {
            public void run() {
                int x = mAdapter.getItemCount();
                int y = mAdapterCount.getItemCount();
                final   TextView textView = v.findViewById(R.id.TaskView);
                textView.setText(String.valueOf(x));
                final TextView count = v.findViewById(R.id.textView);
               // checks if there are remaining open tasks and updates progress percentage
                if(y == 0){
                     if(x == 0){
                         count.setText(x +" % done");
                        // pbProgressBar.setProgress(0);
                         ProgressThread();

                     }else{
                         count.setText(" 100 % done");
                     }
                }else {
                     count.setText(x +" % done");
                }
            }};
        worker.schedule(task, 1, TimeUnit.MILLISECONDS);
    }
    public void deleteNoted(int position) {
        dbs.deleteNoted(notesList.get(position));
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);
        toggleEmptyNotes();
        someMethod();

    }
    private boolean keyboardShown(View rootView) {

        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    private void animateStrikeThrough1(final TextView tv) { }
    public static class NotesAdaptered extends RecyclerView.Adapter<julius.todolist.com.dstv.view.CompletedActivity.NotesAdaptered.MyViewHolder>  {
        private Context context;
        private List<Noted> notesList;
        private CheckBox checkBox;
        private Toolbar toolbar;
        private TaskActivity.NotesAdapter mAdapter;
        private RecyclerView recyclerView;
        private TextView noNotesView;
        private MainActivity mainActivity;
        private DatabaseHelperb db;
        final private float PADDING = 45;
        public class MyViewHolder extends RecyclerView.ViewHolder  {
            public TextView note;
            public TextView time;
            public TextView timestamped;
            public CheckBox checkBox;
            public Toolbar toolbar;
            public Toolbar relativeLayout;
            public CardView cardView;
            public RelativeLayout l;
            public MyViewHolder(final View view) {
                super(view);
                note = view.findViewById(R.id.TextTask);
                timestamped = view.findViewById(R.id.time);
                checkBox = view.findViewById(R.id.checkBox);
                toolbar = view.findViewById(R.id.toolbar22);
                relativeLayout = view.findViewById(R.id.toolbar);
                cardView = view.findViewById(R.id.card);
                l = view.findViewById(R.id.line1); }}

        public NotesAdaptered(Context context, List<Noted> notesList) {
            this.context = context;
            this.notesList = notesList;
        }
        @Override
        public julius.todolist.com.dstv.view.CompletedActivity.NotesAdaptered.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_row_completed, parent, false);
            return new julius.todolist.com.dstv.view.CompletedActivity.NotesAdaptered.MyViewHolder(itemView);        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(final julius.todolist.com.dstv.view.CompletedActivity.NotesAdaptered.MyViewHolder holder, final int position) {
            final Noted noted = notesList.get(position);
            SharedPreferences preferences = this.context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
          boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
            if (useDarkTheme) {
                holder.cardView.setBackgroundColor(Color.parseColor("#2D3137"));
                holder.l.setBackgroundColor(Color.parseColor("#2D3137"));
                holder.note.setTextColor(Color.WHITE);
                holder.note.setBackgroundColor(Color.parseColor("#2D3137"));
                holder.timestamped.setBackgroundColor(Color.parseColor("#2D3137"));
            }
            holder.note.setText(noted.getNoted());
            holder.note.setPaintFlags(holder.note.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            java.text.DateFormat dateFormat = new  java.text.SimpleDateFormat("dd MMM yyyy");
            String todayAsString = dateFormat.format(today);
            holder.checkBox.setChecked(true);
            final String date = noted.getTimestamped();
            if (date.equals(todayAsString)){
                holder.timestamped.setText("Today");
            } else {
                holder.timestamped.setText(noted.getTimestamped());
            }
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                TranslateAnimation moveLeft = new TranslateAnimation(0, 0,
                                        Animation.ABSOLUTE, -1100, Animation.ABSOLUTE,
                                        0.0f, Animation.ABSOLUTE, Animation.ABSOLUTE);
                                moveLeft.setDuration(300);
                                moveLeft.setFillAfter(true);
                                holder.cardView.startAnimation(moveLeft);
                                holder.cardView.setPadding(0, 0, 0, 0);
                            }
                    }, 550); } }); }
        @Override
        public int getItemCount() {
            return notesList.size();
        }}
        //
        public static class NotesAdapter extends RecyclerView.Adapter<julius.todolist.com.dstv.view.CompletedActivity.NotesAdapter.MyViewHolder>  {
            private Context context;
            private List<Note> notesList;
            private CheckBox checkBox;
            private Toolbar toolbar;
            private TaskActivity.NotesAdapter mAdapter;
            private RecyclerView recyclerView;
            private TextView noNotesView;
            private MainActivity mainActivity;
            private DatabaseHelperb db;
            final private float PADDING = 45;
            public class MyViewHolder extends RecyclerView.ViewHolder  {
                public TextView note;
                public TextView time;
                public TextView timestamped;
                public CheckBox checkBox;
                public Toolbar toolbar;
                public Toolbar relativeLayout;
                public CardView cardView;
                public RelativeLayout l;
                public MyViewHolder(final View view) {
                    super(view);
                    note = view.findViewById(R.id.TextTask);
                    timestamped = view.findViewById(R.id.time);
                    checkBox = view.findViewById(R.id.checkBox);
                    toolbar = view.findViewById(R.id.toolbar22);
                    relativeLayout = view.findViewById(R.id.toolbar);
                    cardView = view.findViewById(R.id.card);
                    l = view.findViewById(R.id.line1); }}

            public NotesAdapter(Context context, List<Note> notesList) {
                this.context = context;
                this.notesList = notesList;
            }
            @Override
            public julius.todolist.com.dstv.view.CompletedActivity.NotesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_row_completed, parent, false);
                return new julius.todolist.com.dstv.view.CompletedActivity.NotesAdapter.MyViewHolder(itemView);        }
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onBindViewHolder(final julius.todolist.com.dstv.view.CompletedActivity.NotesAdapter.MyViewHolder holder, final int position) {
                final Note noted = notesList.get(position);
                SharedPreferences preferences = this.context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
                if (useDarkTheme) {
                    holder.cardView.setBackgroundColor(Color.parseColor("#2D3137"));
                    holder.l.setBackgroundColor(Color.parseColor("#2D3137"));
                    holder.note.setTextColor(Color.WHITE);
                    holder.note.setBackgroundColor(Color.parseColor("#2D3137"));
                    holder.timestamped.setBackgroundColor(Color.parseColor("#2D3137"));
                }
                holder.note.setText(noted.getNote());
                holder.note.setPaintFlags(holder.note.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();
                java.text.DateFormat dateFormat = new  java.text.SimpleDateFormat("dd MMM yyyy");
                String todayAsString = dateFormat.format(today);
                holder.checkBox.setChecked(true);
                final String date = noted.getTimestamp();
                if (date.equals(todayAsString)){
                    holder.timestamped.setText("Today");
                } else {
                    holder.timestamped.setText(noted.getTimestamp());
                }
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                TranslateAnimation moveLeft = new TranslateAnimation(0, 0,
                                        Animation.ABSOLUTE, -1100, Animation.ABSOLUTE,
                                        0.0f, Animation.ABSOLUTE, Animation.ABSOLUTE);
                                moveLeft.setDuration(300);
                                moveLeft.setFillAfter(true);
                                holder.cardView.startAnimation(moveLeft);
                                holder.cardView.setPadding(0, 0, 0, 0);
                            }
                        }, 550); } }); }
            @Override
            public int getItemCount() {
                return notesList.size();
            }}
}
