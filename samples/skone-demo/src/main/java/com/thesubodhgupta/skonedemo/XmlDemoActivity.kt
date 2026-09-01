package com.thesubodhgupta.skonedemo

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import io.skone.theme.SKThemes
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.widget.SKTextFieldView
import io.skone.xml.widget.SKTextView

/**
 * External Maven Central XML consumer smoke screen.
 *
 * Uses only APIs present on published `1.4.0-alpha01` `skone-xml`
 * (flagship Text/TextField). No `project(":skone-*")`, no `mavenLocal()`,
 * no playground code.
 *
 * Host note: [AppCompatActivity] requires a `Theme.AppCompat` (or descendant)
 * activity theme — see `Theme.SKOneDemo.Xml`.
 */
class XmlDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SKThemeHelper.install(SKThemes.Light)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val pad = (24 * resources.displayMetrics.density).toInt()
            setPadding(pad, pad, pad, pad)
        }

        root.addView(
            SKTextView(this).apply {
                setSkText("SKOne XML Demo (Maven Central)")
            },
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ),
        )

        root.addView(
            SKTextFieldView(this).apply {
                setFieldId("xml_demo_email")
                setLabel("Email")
                setHint("name@company.com")
                setRequired(true)
            },
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).also {
                it.topMargin = (16 * resources.displayMetrics.density).toInt()
            },
        )

        setContentView(root)
    }
}
