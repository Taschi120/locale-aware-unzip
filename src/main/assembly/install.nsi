!include MUI2.nsh

!define MUI_FINISHPAGE_TEXT "Installation is now complete"

!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_LICENSE COPYING
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

# define installation directory
InstallDir $PROGRAMFILES\LocaleAwareUnzip

RequestExecutionLevel admin

# start default section
Section

    # set the installation directory as the destination for the following actions
    SetOutPath $INSTDIR

    # include all files from the Maven assembly
    File /r *

    # create the uninstaller
    WriteUninstaller "$INSTDIR\uninstall.exe"

    # uninstaller shortcut
    CreateShortcut "$SMPROGRAMS\Uninstall Locale Aware Unzip.lnk" "$INSTDIR\uninstall.exe"

    # app shortcut
    CreateShortcut "$SMPROGRAMS\Locale Aware Unzip.lnk" "$INSTDIR\locale-aware-unzip.exe"
SectionEnd

# uninstaller section start
Section "uninstall"

    # first, delete the uninstaller
    Delete "$INSTDIR\uninstall.exe"

    # second, remove the links from the start menu
    Delete "$SMPROGRAMS\Uninstall Locale Aware Unzip.lnk"
    Delete "$SMPROGRAMS\Locale Aware Unzip.lnk"

 	Delete "$INSTDIR\*"
 	Delete "$INSTDIR\lib\*"

 	RMDir $INSTDIR\lib
    RMDir $INSTDIR
# uninstaller section end
SectionEnd