#summary Describes the special handling of persons as configuration item.

<wiki:toc max_depth="3"/>

= Introduction =

A standard MX person is separated into two parts. First the part defined within
the business administration and the other part defined as business objects. For
the business administration part the same functionality is used as for
[CI_User_PersonAdmin administration persons].

= Steps of the Update Flow =
Also all steps describe for [CI_User_PersonAdmin administration persons].
Further for the business object part, all attributes are reset before the CI
script runs. Because a state change of the person to 'Active' also sent a mail
to the person, the state of the business object is only updated and not reset.
So the person gets the activation mail once (the first time the person is
activated).

= Parameter Definitions =
Also the parameters from [CI_User_PersonAdmin administration persons] could be
used.
|| *Name:* {{{UserPersonIgnoreState}}}            <p>*Parameter:* {{{--ignorePersonStates [PERSON_MATCH]}}} </p><p>Defines the match of persons for whom the manage of states are ignored. This means that the state update for given matching person not managed anymore from the !MxUpdate Update tool and must (could be) managed e.g. in separate MQL update scripts.</p> ||
