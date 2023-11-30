# Event And Categories On-Chain Registration
To create an event with categories you will need to create two or more transactions to broadcast event on chain along 
with the categories on chain. We need one transaction for an event itself and one transaction for each category.

For now CF Cardano Ballot application supports two voting event types: STAKING_BASED and USER_BASED and when creating
an event you have to decide which type you want to use.

In order to create an event, you can create a new class as part of org.cardano.foundation.voting.shell package, e.g.
MyEventCommands.java.

Create a following looking class:
```
@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class MyEventCommands {

    private final static String EVENT_NAME = "MY_EVENT01";

    private final L1SubmissionService l1SubmissionService;

    private final CardanoNetwork network;

    @ShellMethod(key = "create_my_voting_event_pre_prod", value = "Create My Voting Event on a PRE-PROD network.")
    public String createMyVotingEvent() {
        if (network != PREPROD) {
            return "This command can only be run on PRE-PROD network!";
        }

        long startSlot = 43_760_099L;
        long endSlot = startSlot + 604_800L;
        long proposalsRevealSlot = endSlot + 1L;

        var createEventCommand = CreateEventCommand.builder()
                .id(EVENT_NAME)
                .startSlot(Optional.of(startSlot))
                .endSlot(Optional.of(endSlot))
                .votingPowerAsset(Optional.empty())
                .organisers("COMMUNITY ORGANISER")
                .votingEventType(USER_BASED)
                .schemaVersion(V11)
                .allowVoteChanging(false)
                .highLevelEventResultsWhileVoting(true)
                .highLevelCategoryResultsWhileVoting(true)
                .categoryResultsWhileVoting(false)
                .proposalsRevealSlot(Optional.of(proposalsRevealSlot))
                .build();

        l1SubmissionService.submitEvent(createEventCommand);

        return "Created My Event: " + createEventCommand;
    }
    
    @ShellMethod(key = "create_my_voting_category1-pre-prod", value = "Create a my voting category1 on pre-prod.")
    public String create1Category1(@ShellOption String eventId) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        Proposal n1 = Proposal.builder()
                .id("63123e7f-dfc3-481e-bb9d-fed1d9f6e9b9")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("0299d93e-93f2-4bc8-9b40-6dd09343c443")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("fd477fac-ad16-4d2a-91a4-0a4288d3d7aa")
                .name("Option 3")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("CATEGORY_TEST1")
                .event(eventId)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created category: " + createCategoryCommand;
    }
}

It will create an event with the following properties:
- Event name: MY_EVENT01
- Event start slot: 43_760_099
- Event end slot: 44_364_899
- Event proposals reveal slot: 44_364_900
- Event voting power asset: ADA
- Event organisers: COMMUNITY ORGANISER
- Event type: USER_BASED
- Event schema version: V11
- Allow vote changing: false
- High level event results while voting: true
- High level category results while voting: true
- Category results while voting: false

with one 1 category:

- Category name: CATEGORY_TEST1
- Category GDPR protection: true
- Category schema version: V11
- Category proposals: [Option 1, Option 2, Option 3]
```

Note that Proposal IDs are UUIDs and they have to be unique. You can generate them using https://www.uuidgenerator.net/

In order to submit the event you have to start the application for your network (mainnet or pre-prod) and run the commands:
>> create_my_voting_event_pre_prod
>> create_my_voting_category1-pre-prod MY_EVENT01

This should register the event and category on chain and let other components within CF Cardano Ballot platform to "see it".

Alternatively you can register an event of type: STAKE_BASED and here is an example:
```java
@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class MyEventCommands {

    private final static String EVENT_NAME = "MY_EVENT01";

    private final L1SubmissionService l1SubmissionService;

    private final CardanoNetwork network;

    @ShellMethod(key = "create_my_voting_event_pre_prod", value = "Create My Voting Event on a PRE-PROD network.")
    @Order(1)
    public String createMyVotingEvent() {
        if (network != PREPROD) {
            return "This command can only be run on PRE-PROD network!";
        }

        long startEpoch = 101;
        long endSlot = 105;

        var createEventCommand = CreateEventCommand.builder()
                .id(EVENT_NAME)
                .startSlot(Optional.of(startEpoch))
                .endSlot(Optional.of(endSlot))
                .votingPowerAsset(Optional.empty())
                .organisers("COMMUNITY ORGANISER")
                .votingEventType(STAKE_BASED)
                .schemaVersion(V11)
                .allowVoteChanging(false)
                .highLevelEventResultsWhileVoting(true)
                .highLevelCategoryResultsWhileVoting(true)
                .categoryResultsWhileVoting(false)
                .proposalsRevealEpoch(Optional.of(110))
                .build();

        l1SubmissionService.submitEvent(createEventCommand);

        return "Created My Event: " + createEventCommand;
    }

    @ShellMethod(key = "create_my_voting_category1-pre-prod", value = "Create a my voting category1 on pre-prod.")
    @Order(2)
    public String create1Category1(@ShellOption String event) {
        if (network != PREPROD) {
            return "This command can only be run on a PRE-PROD network!";
        }

        Proposal n1 = Proposal.builder()
                .id("63123e7f-dfc3-481e-bb9d-fed1d9f6e9b9")
                .name("Option 1")
                .build();

        Proposal n2 = Proposal.builder()
                .id("0299d93e-93f2-4bc8-9b40-6dd09343c443")
                .name("Option 2")
                .build();

        Proposal n3 = Proposal.builder()
                .id("fd477fac-ad16-4d2a-91a4-0a4288d3d7aa")
                .name("Option 3")
                .build();

        List<Proposal> allProposals = List.of(n1, n2, n3);

        CreateCategoryCommand createCategoryCommand = CreateCategoryCommand.builder()
                .id("CATEGORY_TEST1")
                .event(event)
                .gdprProtection(true)
                .schemaVersion(V11)
                .proposals(allProposals)
                .build();

        if (allProposals.size() != new HashSet<>(allProposals).size()) {
            throw new RuntimeException("Duplicate proposals detected!");
        }

        l1SubmissionService.submitCategory(createCategoryCommand);

        return "Created category: " + createCategoryCommand;
    }
}
```

It will create an event with the following properties:
- Event name: MY_EVENT01
- Event start epoch: 101
- Event end epoch: 105
- Event proposals reveal epoch: 110
- Event voting power asset: ADA
- Event organisers: COMMUNITY ORGANISER
- Event type: STAKE_BASED
- Event schema version: V11
- Allow vote changing: false
- High level event results while voting: true
- High level category results while voting: true
- Category results while voting: false

with one 1 category:
- Category name: CATEGORY_TEST1
- Category GDPR protection: true
- Category schema version: V11
- Category proposals: [Option 1, Option 2, Option 3]

Similarly, in order to submit the event you have to start the application for your network and run the commands:
>> create_my_voting_event_pre_prod
>> create_my_voting_category1-pre-prod MY_EVENT01

This should register the event and category on chain and let other components within CF Cardano Ballot platform to "see it".

If you want to submit main-net event you have start the application with mainnet network and appropriate property files.
