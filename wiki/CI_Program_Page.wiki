#summary Describes the special handling of Pages as configuration item.

<wiki:toc max_depth="3"/>

----

= Introduction =
Pages are typically not used from MX. For a page a mime type typ could be
defined. A page is handled like an [CI_UI_Inquiry inquiry].

----

= MX related Limitations =
Pages could not be linked with properties. This means that symbolic names could
not be defined. A defined symbolic name in the configuration item file is
ignored.

In the exported configuration item file the symbolic name is not included in
the header.

----

= Separator between TCL Update Code and Page Content =
The page content and TCL update code are defined in the same configuration item
update file. Before the separator a comment is defined as information:
{{{
# do not change the next three lines, they are needed as separator information:
}}}
As separator is following text used:
{{{
################################################################################
# PAGE CONTENT                                                                 #
################################################################################
}}}
Below the separator the original content of the page must be defined. No
special escaping is needed.

----

= Handled Page properties =
This page properties could be handled from !MxUpdate:
  * description
  * mime type
  * page content

----

= Steps of the Update Flow =
== Cleanup ==
Following steps are done before the TCL update file is executed:
  * The description is set to zero length string.
  * The mime type is reset.
  * The page content is removed.
== Update ==
The page content defined in the configuration item file is extracted and
written in a temporary file. The path of the file is defined as TCL variable
{{{FILE}}}. When the TCL update code is executed the page content is updated to
the value defined in the file.

----

= Parameter Definitions =
|| *Name:* {{{ProgramPageSeparatorComment}}}      <p>*Default Value:* _see the page content separator_ </p><p>Defines the used comment in front of the separator between the page TCL update code and the page content itself.</p> ||
|| *Name:* {{{ProgramPageSeparatorText}}}         <p>*Default Value:* _see the page content separator_ </p><p>Defines the used text for the separator between the page TCL update code and the page content itself.</p> ||

The parameters could be changed depending on project needs. For further
information see the [UpdatePropertyFileFormat_ParameterDef Parameter Definition Format].

----

= Example =
{{{
################################################################################
# PAGE:
# ~~~~~
# MxUpdate_Test.html
#
# DESCRIPTION:
# ~~~~~~~~~~~~
# MxUpdate test page.
#
# AUTHOR:
# ~~~~~~~
# The MxUpdate Team
################################################################################

mql escape mod page "${NAME}" \
    description "MxUpdate test page." \
    mime "text/html" \
    file [file join "${FILE}"]

# do not change the next three lines, they are needed as separator information:
################################################################################
# PAGE CONTENT                                                                 #
################################################################################

<html>
    <head><title>Title</title></head>
    <body>
        <h1>Test</h1>
    </body>
</html>
}}}
