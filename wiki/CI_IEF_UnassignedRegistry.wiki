#summary Describes the special handling of IEF Unassigned Registry Objects as configuration item.

<wiki:toc max_depth="3"/>

----

= Introduction =

The Unassigned Registry Objects are used from the IEF integrations.

----

= Example (Snippet) =
{{{
################################################################################
# IEFUNASSIGNEDREGISTRY:
# ~~~~~~~~~~~~~~~~~~~~~~
# IEF-UnassignedIntegRegistry________________MxUpdateTest________-
#
# DESCRIPTION:
# ~~~~~~~~~~~~
# -
# MxUpdate Test IEF unassigned registry.
#
# AUTHOR:
# ~~~~~~~
# The MxUpdate Team
################################################################################

mql escape mod bus "${OBJECTID}" \
    description "MxUpdate Test IEF unassigned registry." \
    "DSCAllowedFeatures" "WhereUsed,Lifecycle" \
    "IEF-IntegrationToGCOMapping" ""
}}}
