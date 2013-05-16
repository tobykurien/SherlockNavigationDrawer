/*
 * Copyright 2013 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigationdrawerexample;

import java.util.Locale;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.ActionProvider;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>
 * When a navigation (left) drawer is present, the host activity should detect
 * presses of the action bar's Up affordance as a signal to open and close the
 * navigation drawer. The ActionBarDrawerToggle facilitates this behavior. Items
 * within the drawer should fall into one of two categories:
 * </p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic
 * policies as list or tab navigation in that a view switch does not create
 * navigation history. This pattern should only be used at the root activity of
 * a task, leaving some form of Up navigation active for activities further down
 * the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an
 * alternate parent for Up navigation. This allows a user to jump across an
 * app's navigation hierarchy at will. The application should treat this as it
 * treats Up navigation from a different task, replacing the current task stack
 * using TaskStackBuilder or similar. This is the only form of navigation drawer
 * that should be used outside of the root activity of a task.</li>
 * </ul>
 * <p/>
 * <p>
 * Right side drawers should be used for actions, not navigation. This follows
 * the pattern established by the Action Bar that navigation should be to the
 * left and actions to the right. An action should be an operation performed on
 * the current contents of the window, for example enabling or disabling a data
 * overlay on top of the current content.
 * </p>
 */
public class MainActivity extends SherlockFragmentActivity {
   private DrawerLayout mDrawerLayout;
   private ListView mDrawerList;
   private ActionBarDrawerToggle mDrawerToggle;

   private CharSequence mDrawerTitle;
   private CharSequence mTitle;
   private String[] mPlanetTitles;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mTitle = mDrawerTitle = getTitle();
      mPlanetTitles = getResources().getStringArray(R.array.planets_array);
      mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
      mDrawerList = (ListView) findViewById(R.id.left_drawer);

      // set a custom shadow that overlays the main content when the drawer
      // opens
      mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
      // set up the drawer's list view with items and click listener
      mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));
      mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

      // enable ActionBar app icon to behave as action to toggle nav drawer
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);

      // ActionBarDrawerToggle ties together the the proper interactions
      // between the sliding drawer and the action bar app icon
      mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
      mDrawerLayout, /* DrawerLayout object */
      R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
      R.string.drawer_open, /* "open drawer" description for accessibility */
      R.string.drawer_close /* "close drawer" description for accessibility */
      ) {
         public void onDrawerClosed(View view) {
            getSupportActionBar().setTitle(mTitle);
            supportInvalidateOptionsMenu(); // creates call to
                                            // onPrepareOptionsMenu()
         }

         public void onDrawerOpened(View drawerView) {
            getSupportActionBar().setTitle(mDrawerTitle);
            supportInvalidateOptionsMenu(); // creates call to
                                            // onPrepareOptionsMenu()
         }
      };
      mDrawerLayout.setDrawerListener(mDrawerToggle);

      if (savedInstanceState == null) {
         selectItem(0);
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getSupportMenuInflater();
      inflater.inflate(R.menu.main, menu);
      return super.onCreateOptionsMenu(menu);
   }

   /* Called whenever we call invalidateOptionsMenu() */
   @Override
   public boolean onPrepareOptionsMenu(Menu menu) {
      // If the nav drawer is open, hide action items related to the content
      // view
      boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
      menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
      return super.onPrepareOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(final MenuItem item) {
      // The action bar home/up action should open or close the drawer.
      // ActionBarDrawerToggle will take care of this.
      if (mDrawerToggle.onOptionsItemSelected(getMenuItem(item))) { 
         return true; 
      }

      // Handle action buttons
      switch (item.getItemId()) {
         case R.id.action_websearch:
            // create intent to perform web search for this planet
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, getSupportActionBar().getTitle());
            // catch event that there's no activity to handle intent
            if (intent.resolveActivity(getPackageManager()) != null) {
               startActivity(intent);
            } else {
               Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            }
            return true;
         default:
            return super.onOptionsItemSelected(item);
      }
   }

   private android.view.MenuItem getMenuItem(final MenuItem item) {
      return new android.view.MenuItem() {
         @Override
         public int getItemId() {
            return item.getItemId();
         }

         public boolean isEnabled() {
            return true;
         }

         @Override
         public boolean collapseActionView() {
            // TODO Auto-generated method stub
            return false;
         }

         @Override
         public boolean expandActionView() {
            // TODO Auto-generated method stub
            return false;
         }

         @Override
         public ActionProvider getActionProvider() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public View getActionView() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public char getAlphabeticShortcut() {
            // TODO Auto-generated method stub
            return 0;
         }

         @Override
         public int getGroupId() {
            // TODO Auto-generated method stub
            return 0;
         }

         @Override
         public Drawable getIcon() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public Intent getIntent() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public ContextMenuInfo getMenuInfo() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public char getNumericShortcut() {
            // TODO Auto-generated method stub
            return 0;
         }

         @Override
         public int getOrder() {
            // TODO Auto-generated method stub
            return 0;
         }

         @Override
         public SubMenu getSubMenu() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public CharSequence getTitle() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public CharSequence getTitleCondensed() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public boolean hasSubMenu() {
            // TODO Auto-generated method stub
            return false;
         }

         @Override
         public boolean isActionViewExpanded() {
            // TODO Auto-generated method stub
            return false;
         }

         @Override
         public boolean isCheckable() {
            // TODO Auto-generated method stub
            return false;
         }

         @Override
         public boolean isChecked() {
            // TODO Auto-generated method stub
            return false;
         }

         @Override
         public boolean isVisible() {
            // TODO Auto-generated method stub
            return false;
         }

         @Override
         public android.view.MenuItem setActionProvider(ActionProvider actionProvider) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setActionView(View view) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setActionView(int resId) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setAlphabeticShortcut(char alphaChar) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setCheckable(boolean checkable) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setChecked(boolean checked) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setEnabled(boolean enabled) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setIcon(Drawable icon) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setIcon(int iconRes) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setIntent(Intent intent) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setNumericShortcut(char numericChar) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setShortcut(char numericChar, char alphaChar) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public void setShowAsAction(int actionEnum) {
            // TODO Auto-generated method stub

         }

         @Override
         public android.view.MenuItem setShowAsActionFlags(int actionEnum) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setTitle(CharSequence title) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setTitle(int title) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setTitleCondensed(CharSequence title) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public android.view.MenuItem setVisible(boolean visible) {
            // TODO Auto-generated method stub
            return null;
         }
      };
   }

   /* The click listner for ListView in the navigation drawer */
   private class DrawerItemClickListener implements ListView.OnItemClickListener {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         selectItem(position);
      }
   }

   private void selectItem(int position) {
      // update the main content by replacing fragments
      Fragment fragment = new PlanetFragment();
      Bundle args = new Bundle();
      args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
      fragment.setArguments(args);

      FragmentManager fragmentManager = getSupportFragmentManager();
      fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

      // update selected item and title, then close the drawer
      mDrawerList.setItemChecked(position, true);
      setTitle(mPlanetTitles[position]);
      mDrawerLayout.closeDrawer(mDrawerList);
   }

   @Override
   public void setTitle(CharSequence title) {
      mTitle = title;
      getSupportActionBar().setTitle(mTitle);
   }

   /**
    * When using the ActionBarDrawerToggle, you must call it during
    * onPostCreate() and onConfigurationChanged()...
    */

   @Override
   protected void onPostCreate(Bundle savedInstanceState) {
      super.onPostCreate(savedInstanceState);
      // Sync the toggle state after onRestoreInstanceState has occurred.
      mDrawerToggle.syncState();
   }

   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      // Pass any configuration change to the drawer toggls
      mDrawerToggle.onConfigurationChanged(newConfig);
   }

   /**
    * Fragment that appears in the "content_frame", shows a planet
    */
   public static class PlanetFragment extends SherlockFragment {
      public static final String ARG_PLANET_NUMBER = "planet_number";

      public PlanetFragment() {
         // Empty constructor required for fragment subclasses
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
         int i = getArguments().getInt(ARG_PLANET_NUMBER);
         String planet = getResources().getStringArray(R.array.planets_array)[i];

         int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()), "drawable", getActivity().getPackageName());
         ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
         getActivity().setTitle(planet);
         return rootView;
      }
   }
}