package com.example.chatit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.chatit.ui.main.SectionsPagerAdapter;

public class TabAct extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);
        ImageButton menuBtn = findViewById(R.id.menu_tab);


        tabs.setSelectedTabIndicatorColor(Color.parseColor("#FF000000"));
        tabs.setSelectedTabIndicatorHeight((int) (5* getResources().getDisplayMetrics().density));
        tabs.setTabTextColors(Color.parseColor("#FF000000"),Color.parseColor("#ffffff"));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TabAct.this,Profile.class);
                startActivity(intent);

            }
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(TabAct.this,view);
                popupMenu.setOnMenuItemClickListener(TabAct.this);
                popupMenu.inflate(R.menu.menufile);
                popupMenu.show();


            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.newgroup:

                Intent intent1 = new Intent(TabAct.this,GroupName.class);
                startActivity(intent1);
                return true;

                case R.id.profile:

                    Intent intent = new Intent(TabAct.this,Profile.class);
                    startActivity(intent);
                    return true;

                case R.id.privacy:
                Toast.makeText(this, "privacy clicked", Toast.LENGTH_SHORT).show();
                break;

        }

        return false;
    }
}