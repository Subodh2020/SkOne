# SKDialog

Generic modal — Compose `SKDialog` / XML `SKDialogHost` (programmatic).

Host owns `visible` / show-dismiss. Content slot is free-form.

**Intentional surface difference:** XML uses a programmatic host (not an inflate-only View) because Android dialogs are window-level.
