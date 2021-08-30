package arunkbabu90.popmovies.ui.activity

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import arunkbabu90.popmovies.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class PopMoviesFullTest {

    @get:Rule
    var mActivityTestRule: ActivityScenarioRule<LoginActivity> = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun popMoviesFullTest() {
        val textView = onView(
            allOf(
                withId(R.id.tv_login_guest), withText("Login as Guest"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Login as Guest")))

        val textView2 = onView(
            allOf(
                withId(R.id.tv_login_guest), withText("Login as Guest"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView2.check(matches(isDisplayed()))

        val materialTextView = onView(
            allOf(
                withId(R.id.tv_login_guest), withText("Login as Guest"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    5
                )
            )
        )
        materialTextView.perform(scrollTo(), click())

        val textView3 = onView(
            allOf(
                withId(R.id.mnu_profile_name), withText("GUEST"),
                withParent(withParent(withId(R.id.toolbar_main))),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("GUEST")))

        val textView4 = onView(
            allOf(
                withId(R.id.mnu_profile_name), withText("GUEST"),
                withParent(withParent(withId(R.id.toolbar_main))),
                isDisplayed()
            )
        )
        textView4.check(matches(isDisplayed()))

        val textView5 = onView(
            allOf(
                withText("Now Playing"),
                withParent(
                    allOf(
                        withContentDescription("Now Playing"),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView5.check(matches(withText("Now Playing")))

        val textView6 = onView(
            allOf(
                withId(R.id.mnu_sign_out), withContentDescription("Sign Out"),
                withParent(withParent(withId(R.id.toolbar_main))),
                isDisplayed()
            )
        )
        textView6.check(matches(isDisplayed()))

        val actionMenuItemView = onView(
            allOf(
                withId(R.id.mnu_sign_out), withContentDescription("Sign Out"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar_main),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        actionMenuItemView.perform(click())

        val textView7 = onView(
            allOf(
                withId(R.id.tv_login_guest), withText("Login as Guest"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView7.check(matches(withText("Login as Guest")))

        val textView8 = onView(
            allOf(
                withId(R.id.tv_login_guest), withText("Login as Guest"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView8.check(matches(isDisplayed()))

        val textView9 = onView(
            allOf(
                withId(R.id.tv_sign_up), withText("Sign Up"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView9.check(matches(withText("Sign Up")))

        val textView10 = onView(
            allOf(
                withId(R.id.tv_sign_up), withText("Sign Up"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView10.check(matches(isDisplayed()))

        val textView11 = onView(
            allOf(
                withId(R.id.login_textView), withText("Login"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView11.check(matches(withText("Login")))

        val textView12 = onView(
            allOf(
                withId(R.id.login_textView), withText("Login"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView12.check(matches(isDisplayed()))

        val button = onView(
            allOf(
                withId(R.id.btn_login), withText("Login"),
                withParent(
                    allOf(
                        withId(R.id.login_layout),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        val editText = onView(
            allOf(
                withId(R.id.et_email), withText("Email"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText.check(matches(withText("Email")))

        val editText2 = onView(
            allOf(
                withId(R.id.et_email), withText("Email"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText2.check(matches(isDisplayed()))

        val editText3 = onView(
            allOf(
                withId(R.id.et_password), withText("Password"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText3.check(matches(withText("Password")))

        val editText4 = onView(
            allOf(
                withId(R.id.et_password), withText("Password"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText4.check(matches(isDisplayed()))

        val customInputTextField = onView(
            allOf(
                withId(R.id.et_email),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("com.google.android.material.textfield.TextInputLayout")),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        customInputTextField.perform(click())

        val customInputTextField2 = onView(
            allOf(
                withId(R.id.et_email),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("com.google.android.material.textfield.TextInputLayout")),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        customInputTextField2.perform(replaceText("arunkbabu90@gmail.com"), closeSoftKeyboard())

        val customInputTextField3 = onView(
            allOf(
                withId(R.id.et_email), withText("arunkbabu90@gmail.com"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("com.google.android.material.textfield.TextInputLayout")),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        customInputTextField3.perform(pressImeActionButton())

        val customInputTextField4 = onView(
            allOf(
                withId(R.id.et_password),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("com.google.android.material.textfield.TextInputLayout")),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        customInputTextField4.perform(click())

        val customInputTextField5 = onView(
            allOf(
                withId(R.id.et_password),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("com.google.android.material.textfield.TextInputLayout")),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        customInputTextField5.perform(replaceText("aaaaaaaa"), closeSoftKeyboard())

        pressBack()

        val materialButton = onView(
            allOf(
                withId(R.id.btn_login), withText("Login"),
                childAtPosition(
                    allOf(
                        withId(R.id.login_layout),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            3
                        )
                    ),
                    2
                )
            )
        )
        materialButton.perform(scrollTo(), click())

        val textView13 = onView(
            allOf(
                withId(R.id.mnu_profile_name), withContentDescription("Profile"),
                withParent(withParent(withId(R.id.toolbar_main))),
                isDisplayed()
            )
        )
        textView13.check(matches(isDisplayed()))

        val textView14 = onView(
            allOf(
                withId(R.id.mnu_favourites), withContentDescription("Favourites"),
                withParent(withParent(withId(R.id.toolbar_main))),
                isDisplayed()
            )
        )
        textView14.check(matches(isDisplayed()))

        val textView15 = onView(
            allOf(
                withId(R.id.mnu_sign_out), withContentDescription("Sign Out"),
                withParent(withParent(withId(R.id.toolbar_main))),
                isDisplayed()
            )
        )
        textView15.check(matches(isDisplayed()))

        val textView16 = onView(
            allOf(
                withText("Now Playing"),
                withParent(
                    allOf(
                        withContentDescription("Now Playing"),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView16.check(matches(withText("Now Playing")))

        val textView17 = onView(
            allOf(
                withText("Now Playing"),
                withParent(
                    allOf(
                        withContentDescription("Now Playing"),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView17.check(matches(isDisplayed()))

        val tabView = onView(
            allOf(
                withContentDescription("Popular"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.movie_tab_layout),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        tabView.perform(click())

        val tabView2 = onView(
            allOf(
                withContentDescription("Top Rated"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.movie_tab_layout),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        tabView2.perform(click())

        val tabView3 = onView(
            allOf(
                withContentDescription("Search"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.movie_tab_layout),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        tabView3.perform(click())

        val searchAutoComplete = onView(
            allOf(
                withId(R.id.search_src_text),
                childAtPosition(
                    allOf(
                        withId(R.id.search_plate),
                        childAtPosition(
                            withId(R.id.search_edit_frame),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        searchAutoComplete.perform(click())

        val searchAutoComplete2 = onView(
            allOf(
                withId(R.id.search_src_text),
                childAtPosition(
                    allOf(
                        withId(R.id.search_plate),
                        childAtPosition(
                            withId(R.id.search_edit_frame),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        searchAutoComplete2.perform(click())

        val searchAutoComplete3 = onView(
            allOf(
                withId(R.id.search_src_text),
                childAtPosition(
                    allOf(
                        withId(R.id.search_plate),
                        childAtPosition(
                            withId(R.id.search_edit_frame),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        searchAutoComplete3.perform(replaceText("interstellar"), closeSoftKeyboard())

        val editText6 = onView(
            allOf(
                withId(R.id.search_src_text), withText("interstellar"),
                withParent(
                    allOf(
                        withId(R.id.search_plate),
                        withParent(withId(R.id.search_edit_frame))
                    )
                ),
                isDisplayed()
            )
        )
        editText6.check(matches(withText("interstellar")))

        val editText7 = onView(
            allOf(
                withId(R.id.search_src_text), withText("interstellar"),
                withParent(
                    allOf(
                        withId(R.id.search_plate),
                        withParent(withId(R.id.search_edit_frame))
                    )
                ),
                isDisplayed()
            )
        )
        editText7.check(matches(isDisplayed()))

        val imageView = onView(
            allOf(
                withId(R.id.search_close_btn), withContentDescription("Clear query"),
                withParent(
                    allOf(
                        withId(R.id.search_plate),
                        withParent(withId(R.id.search_edit_frame))
                    )
                ),
                isDisplayed()
            )
        )
        imageView.check(matches(isDisplayed()))

        val imageView2 = onView(
            allOf(
                withId(R.id.search_close_btn), withContentDescription("Clear query"),
                withParent(
                    allOf(
                        withId(R.id.search_plate),
                        withParent(withId(R.id.search_edit_frame))
                    )
                ),
                isDisplayed()
            )
        )
        imageView2.check(matches(isDisplayed()))

        val searchAutoComplete4 = onView(
            allOf(
                withId(R.id.search_src_text), withText("interstellar"),
                childAtPosition(
                    allOf(
                        withId(R.id.search_plate),
                        childAtPosition(
                            withId(R.id.search_edit_frame),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        searchAutoComplete4.perform(pressImeActionButton())

        val actionMenuItemView2 = onView(
            allOf(
                withId(R.id.mnu_profile_name), withContentDescription("Profile"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar_main),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemView2.perform(click())

        val button2 = onView(
            allOf(
                withId(R.id.btn_sign_out), withText("SIGN OUT"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        button2.check(matches(isDisplayed()))

        val imageView3 = onView(
            allOf(
                withId(R.id.iv_profile_dp),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        imageView3.check(matches(isDisplayed()))

        val imageButton = onView(
            allOf(
                withId(R.id.fab_doc_profile_dp_edit),
                withContentDescription("Edit Profile Picture"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        imageButton.check(matches(isDisplayed()))

        val imageButton2 = onView(
            allOf(
                withId(R.id.fab_doc_profile_dp_edit),
                withContentDescription("Edit Profile Picture"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        imageButton2.check(matches(isDisplayed()))

        pressBack()

        val actionMenuItemView3 = onView(
            allOf(
                withId(R.id.mnu_favourites), withContentDescription("Favourites"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar_main),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        actionMenuItemView3.perform(click())

        pressBack()

        val actionMenuItemView4 = onView(
            allOf(
                withId(R.id.mnu_sign_out), withContentDescription("Sign Out"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar_main),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        actionMenuItemView4.perform(click())

        val textView18 = onView(
            allOf(
                withId(R.id.login_textView), withText("Login"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView18.check(matches(withText("Login")))

        val textView19 = onView(
            allOf(
                withId(R.id.login_textView), withText("Login"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView19.check(matches(isDisplayed()))

        val textView20 = onView(
            allOf(
                withId(R.id.tv_login_guest), withText("Login as Guest"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView20.check(matches(withText("Login as Guest")))

        val textView21 = onView(
            allOf(
                withId(R.id.tv_login_guest), withText("Login as Guest"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView21.check(matches(isDisplayed()))

        val button3 = onView(
            allOf(
                withId(R.id.btn_login), withText("Login"),
                withParent(
                    allOf(
                        withId(R.id.login_layout),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        button3.check(matches(isDisplayed()))

        val textView22 = onView(
            allOf(
                withId(R.id.tv_forgot_password), withText("Forgot Password?"),
                withParent(
                    allOf(
                        withId(R.id.login_layout),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView22.check(matches(withText("Forgot Password?")))

        val textView23 = onView(
            allOf(
                withId(R.id.tv_forgot_password), withText("Forgot Password?"),
                withParent(
                    allOf(
                        withId(R.id.login_layout),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView23.check(matches(isDisplayed()))

        val textView24 = onView(
            allOf(
                withId(R.id.tv_forgot_password), withText("Forgot Password?"),
                withParent(
                    allOf(
                        withId(R.id.login_layout),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView24.check(matches(isDisplayed()))

        val materialTextView2 = onView(
            allOf(
                withId(R.id.tv_forgot_password), withText("Forgot Password?"),
                childAtPosition(
                    allOf(
                        withId(R.id.login_layout),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            3
                        )
                    ),
                    3
                )
            )
        )
        materialTextView2.perform(scrollTo(), click())

        val textView25 = onView(
            allOf(
                withId(R.id.textView1), withText("Reset Password"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        textView25.check(matches(withText("Reset Password")))

        val textView26 = onView(
            allOf(
                withId(R.id.textView1), withText("Reset Password"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        textView26.check(matches(isDisplayed()))

        val button4 = onView(
            allOf(
                withId(R.id.btn_forgot_password_sent), withText("Send Verification"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        button4.check(matches(isDisplayed()))

        val editText8 = onView(
            allOf(
                withId(R.id.et_forgot_password_email), withText("Email"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText8.check(matches(withText("Email")))

        val editText9 = onView(
            allOf(
                withId(R.id.et_forgot_password_email), withText("Email"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText9.check(matches(isDisplayed()))

        val editText10 = onView(
            allOf(
                withId(R.id.et_forgot_password_email), withText("Email"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText10.check(matches(isDisplayed()))

        pressBack()

        val materialTextView3 = onView(
            allOf(
                withId(R.id.tv_sign_up), withText("Sign Up"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    4
                )
            )
        )
        materialTextView3.perform(scrollTo(), click())

        val button5 = onView(
            allOf(
                withId(R.id.btn_sign_up_next), withText("CREATE"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        button5.check(matches(isDisplayed()))

        val textView27 = onView(
            allOf(
                withId(R.id.sign_up_heading), withText("Sign Up"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        textView27.check(matches(withText("Sign Up")))

        val textView28 = onView(
            allOf(
                withId(R.id.sign_up_heading), withText("Sign Up"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        textView28.check(matches(isDisplayed()))

        val editText11 = onView(
            allOf(
                withId(R.id.et_sign_up_full_name), withText("Full Name"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText11.check(matches(withText("Full Name")))

        val editText12 = onView(
            allOf(
                withId(R.id.et_sign_up_full_name), withText("Full Name"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText12.check(matches(isDisplayed()))

        val editText13 = onView(
            allOf(
                withId(R.id.et_sign_up_email), withText("Email"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText13.check(matches(withText("Email")))

        val editText14 = onView(
            allOf(
                withId(R.id.et_sign_up_email), withText("Email"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText14.check(matches(isDisplayed()))

        val editText15 = onView(
            allOf(
                withId(R.id.et_sign_up_password), withText("Password"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText15.check(matches(withText("Password")))

        val editText16 = onView(
            allOf(
                withId(R.id.et_sign_up_password), withText("Password"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText16.check(matches(isDisplayed()))

        val editText17 = onView(
            allOf(
                withId(R.id.et_sign_up_password_confirm), withText("Confirm Password"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText17.check(matches(withText("Confirm Password")))

        val editText18 = onView(
            allOf(
                withId(R.id.et_sign_up_password_confirm), withText("Confirm Password"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText18.check(matches(isDisplayed()))

        val editText19 = onView(
            allOf(
                withId(R.id.et_sign_up_password_confirm), withText("Confirm Password"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText19.check(matches(isDisplayed()))

        pressBack()

        val button6 = onView(
            allOf(
                withId(R.id.btn_login), withText("Login"),
                withParent(
                    allOf(
                        withId(R.id.login_layout),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        button6.check(matches(isDisplayed()))

        val textView29 = onView(
            allOf(
                withId(R.id.login_textView), withText("Login"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView29.check(matches(withText("Login")))

        val textView30 = onView(
            allOf(
                withId(R.id.login_textView), withText("Login"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView30.check(matches(isDisplayed()))

        val textView31 = onView(
            allOf(
                withId(R.id.tv_login_guest), withText("Login as Guest"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView31.check(matches(withText("Login as Guest")))

        val textView32 = onView(
            allOf(
                withId(R.id.tv_login_guest), withText("Login as Guest"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView32.check(matches(isDisplayed()))

        val textView33 = onView(
            allOf(
                withId(R.id.tv_login_guest), withText("Login as Guest"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView33.check(matches(isDisplayed()))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
