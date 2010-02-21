#summary Describes the special handling of Trigger Groups as configuration item.

<wiki:toc max_depth="3"/>

----

= Introduction =
Trigger Groups could be used to group trigger parameter definitions. So they are
used as documentation.

----

= Parameter Definitions =
|| *Name:* {{{InstallTriggerGroupPolicyDesc}}}    <p>*Default Value:* Policy used for the group trigger definitions. </p>      <p>Defines the description which is used for the policy of trigger groups.</p> ||
|| *Name:* {{{InstallTriggerGroupRelationDesc}}}  <p>*Default Value:* Relationship used for the group trigger definitions. </p><p>Defines the description which is used for the relationship of trigger groups.</p> ||
|| *Name:* {{{InstallTriggerGroupTypeDesc}}}      <p>*Default Value:* Type used for the group trigger definitions. </p>        <p>Defines the description which is used for the type of trigger groups.</p> ||


----

= Example =
{{{
################################################################################
# TRIGGERGROUP:
# ~~~~~~~~~~~~~
# MxUpdateTestTypeCreateAction________Type
#
# DESCRIPTION:
# ~~~~~~~~~~~~
# Type
# Trigger Group for all triggers on type 'MxUpdateTestTypeCreateAction'.
#
# AUTHOR:
# ~~~~~~~
# The MxUpdate Team
################################################################################

mql mod bus "${OBJECTID}" \
    description "Trigger Group for all triggers on type 'MxUpdateTestTypeCreateAction'."
mql connect bus "${OBJECTID}" \
    relationship "MxUpdate Trigger Group Relationship" \
    to "eService Trigger Program Parameters" "TypeMxUpdateTestTypeCreateAction" "TestTrigger"
}}}
