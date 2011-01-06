#summary Describes the special handling of IEF Mass Promote Configuration Objects as configuration item.

<wiki:toc max_depth="3"/>

----

= Introduction =

The Mass Promote Configuration Objects are used from the IEF integrations.

----

= Example (Snippet) =
{{{
################################################################################
# IEFMASSPROMOTECONFIG:
# ~~~~~~~~~~~~~~~~~~~~~
# IEF-MassPromoteConfig________________MxUpdateTest________-
#
# DESCRIPTION:
# ~~~~~~~~~~~~
# -
# MxUpdate Test IEF mass promote configuration.
#
# AUTHOR:
# ~~~~~~~
# The MxUpdate Team
################################################################################

mql escape mod bus "${OBJECTID}" \
    description "MxUpdate Test IEF mass promote configuration." \
    "IEF-MassPromoteValidationRuleJPO" "" \
    "IEF-ValidateObjectPromotion" "TRUE" \
    "IEF-ValidateObjectSelection" "TRUE"
}}}
