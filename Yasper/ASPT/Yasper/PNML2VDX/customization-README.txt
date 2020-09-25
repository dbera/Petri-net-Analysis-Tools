PNML2VDX translation operates by modifying a predefined VDX document;
therefore, you can customize it by modifying that document
prior to executing the translation.

As long as the edits are minor, the translation will still work.
(What exactly constitutes a 'minor' edit is not well defined at this moment.)

However!

Visio 2002 and Visio 2003 have incompatible .vdx formats.
It turns out they cannot read each other's vdx without throwing
warning messages and losing information.

Therefore, the code employs two versions of the predefined document:

  2002.vdx  (for use with Visio 2002)
  2003.vdx  (for use with Visio 2003)

and a third document is used for editing:

  2002.vst

In order to maintain compatibility with both Visio 2002 and Visio 2003,
to customize, always use Visio 2002 and always edit 2002.vst.
(Yes, this *is* a Visio template, not a regular Visio document.)

Then
  + save the result (as the template 2002.vst),
  + Save As 2002.vdx,
  + open Visio 2003,
  + read 2002.vst (*not* 2002.vdx!), and
  + Save As 2003.vdx (*not* 2002.vdx or 2003.vtx).


(TODO: script the vst->vdx conversion with WSH or Interop.)