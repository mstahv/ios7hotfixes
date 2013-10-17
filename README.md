# iOS7 Home Screen Web App Hot Fixes

Releases can be found from Vaadin Directory:
http://vaadin.com/directory#addon/ios-7-hotfixes

iOS7 release had/has some extremely serious regressions that concern especially so called "home screen web applications". This is the app type that was originally the one and only method for making "apps" for iPhone. The Web is [full](http://www.mobilexweb.com/blog/safari-ios7-html5-problems-apis-review) of [lists](http://www.infoworld.com/t/html5/bad-news-ios-7s-html5-full-of-bugs-227685) of these issues so I don't see point of repeating them here. If you have ended up here, you probably know them too well already.

This add-on tries to overcome the most critical ones to make [Vaadin TouchKit](https://vaadin.com/add-ons/touchkit) usable on iOS7 devices. The add-on itself is not TouchKit specific, so it might well fix other mobile GWT apps as well. The issues are almost certainly to be fixed by Apple in further releases, so keep up to date with these bugs, and write bug reports to them and remove these hacks as early as possible. In a long term, workarounds like these are not good for anybody.

This add-on should be compatible with TouchKit versions: 2.1.x, 3.x and also with latest series 4 development releases. For these projects just adding this dependency (or jar to Eclipse WTP project) and doing full rebuild should apply fixes. Note that app might still need dual reloads by browser to get new updates. If you use "manually edited" Vaadin widetset or you are using this add-on in generic GWT apps, inherit "org.vaadin.addon.ios7fixes.Ios7HotFixesWidgetset" in your GWT module "the ....gwt.xml file" file and do GWT recompilation.

Currently included fixes:
 * "somewhat working" native viewport settings
  * This is a cause for several critical bugs that cause odd content clipping and inaccessible text fields, see e.g.  http://www.youtube.com/watch?v=gnfdusUsEAs
  * Workaround description: Adds height=device-height rule to your viewport meta data and adds some extra hacks to bypass issues that remain or are caused by that.
 * Missing window.alert function has a "polyfill". Confirm and prompt are impossible to workaround (patches wellcome), but also there user/developer gets an error message that something went wrong.

## License & Author

Licensed under Apache 2.0 License

## Guarantee

I cannot guarantee, but I can make a sophisticated guess that this piece of software is at least as stable as mobile web apps on iOS7 :-)

