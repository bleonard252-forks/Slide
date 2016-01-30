package me.ccrama.redditslide.Fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.dean.jraw.models.Submission;

import me.ccrama.redditslide.Activities.CommentsScreen;
import me.ccrama.redditslide.Activities.CommentsScreenPopup;
import me.ccrama.redditslide.Activities.Website;
import me.ccrama.redditslide.ContentType;
import me.ccrama.redditslide.DataShare;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.Views.MediaVideoView;
import me.ccrama.redditslide.Views.PopulateSubmissionViewHolder;
import me.ccrama.redditslide.util.GifUtils;


/**
 * Created by ccrama on 6/2/2015.
 */
public class Gif extends Fragment {

    private int i = 0;
    private View placeholder;
    private Submission s;
    private View gif;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (!isVisibleToUser)   // If we are becoming invisible, then...
            {
                ((MediaVideoView) gif).pause();
                gif.setVisibility(View.GONE);
            }

            if (isVisibleToUser) // If we are becoming visible, then...
            {
                ((MediaVideoView) gif).start();
                gif.setVisibility(View.VISIBLE);

            }
        }
    }
    ViewGroup rootView;
    ProgressBar loader;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       rootView = (ViewGroup) inflater.inflate(
                R.layout.submission_gifcard, container, false);
        loader = (ProgressBar) rootView.findViewById(R.id.gifprogress);

        TextView title = (TextView) rootView.findViewById(R.id.title);
        TextView desc = (TextView) rootView.findViewById(R.id.desc);

        title.setText(s.getTitle());
        desc.setText(s.getSubredditName() + getString(R.string.submission_properties_seperator) + s.getAuthor() + " " + TimeUtils.getTimeAgo(s.getCreated().getTime(), getContext()) +
                getString(R.string.submission_properties_seperator) +
                PopulateSubmissionViewHolder.getSubmissionScoreString(s.getScore(), getActivity().getResources(), s)
                + getString(R.string.submission_properties_seperator)
                + getActivity().getResources().getQuantityString(R.plurals.submission_comment_count, s.getCommentCount(), s.getCommentCount())
                        + getString(R.string.submission_properties_seperator)
                        + Website.getDomainName(s.getUrl()));
        ContentType.ImageType type = ContentType.getImageType(s);

        placeholder = rootView.findViewById(R.id.placeholder);
        gif = rootView.findViewById(R.id.gif);


        gif.setVisibility(View.VISIBLE);
        final MediaVideoView v = (MediaVideoView) gif;
        v.clearFocus();


        String dat = s.getUrl();


        if(dat.contains("webm") && dat.contains("imgur")){
            dat = dat.replace("webm", "gifv");
        }
        if(dat.contains("mp4") && dat.contains("imgur")){
            dat = dat.replace("mp4", "gifv");
        }

        if (dat.endsWith("v")) {
            dat = dat.substring(0, dat.length() - 1);
        } else if (dat.contains("gfycat")) {
            dat = dat.substring(3, dat.length());
        }
        new GifUtils.AsyncLoadGif(getActivity(), (MediaVideoView) v.findViewById(R.id.gif), loader, v.findViewById(R.id.placeholder),null, false).execute(dat);




        rootView.findViewById(R.id.base).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Reddit.tabletUI && getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Intent i2 = new Intent(getActivity(), CommentsScreenPopup.class);
                    i2.putExtra("page", i);
                    (getActivity()).startActivity(i2);

                } else {
                    Intent i2 = new Intent(getActivity(), CommentsScreen.class);
                    i2.putExtra("page", i);
                    i2.putExtra("subreddit", s.getSubredditName());
                    (getActivity()).startActivity(i2);
                }
            }
        });
        return rootView;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        i = bundle.getInt("page", 0);
        s = DataShare.sharedSubreddit.get(bundle.getInt("page", 0));

    }



}
