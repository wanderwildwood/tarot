# Privacy

Tarot collects nothing and sends nothing anywhere. It asks for no permissions, and has no
internet permission, so it cannot.

It keeps, in its own private storage on the phone: the journal of readings (each one's date,
spread, cards, orientation and note) and its two settings, reversed cards on or off and the
letters and page dots shown or hidden. None of it is
backed up by the app, and uninstalling it removes all of it.

**Export the journal** writes a copy to a file you choose through the phone's own file picker,
and only when you ask.

The About dialog's last line opens a web address in your browser, and only when you press it.

The one permission in its manifest is not one you are asked for: Android's support library adds
it to every app built with it (`DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`), so that the app's own
internal messages can reach only the app itself.
