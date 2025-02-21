package seedu.awe.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.awe.commons.core.Messages.MESSAGE_CREATEGROUPCOMMAND_EMPTY_GROUP;
import static seedu.awe.commons.core.Messages.MESSAGE_CREATEGROUPCOMMAND_INVALID_NAMES;
import static seedu.awe.commons.core.Messages.MESSAGE_GROUPREMOVECONTACTCOMMAND_USAGE;
import static seedu.awe.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.awe.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.awe.logic.commands.CommandTestUtil.VALID_NAME_ALICE;
import static seedu.awe.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.awe.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.awe.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.awe.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.awe.testutil.Assert.assertThrows;
import static seedu.awe.testutil.TypicalPersons.ALICE;
import static seedu.awe.testutil.TypicalPersons.AMY;
import static seedu.awe.testutil.TypicalPersons.BOB;
import static seedu.awe.testutil.TypicalPersons.CARL;
import static seedu.awe.testutil.TypicalPersons.ELLE;
import static seedu.awe.testutil.TypicalPersons.FIONA;
import static seedu.awe.testutil.TypicalPersons.GEORGE;
import static seedu.awe.testutil.TypicalPersons.NONEXISTENTPERSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.awe.commons.core.Messages;
import seedu.awe.logic.commands.GroupRemoveContactCommand;
import seedu.awe.logic.parser.exceptions.EmptyGroupException;
import seedu.awe.logic.parser.exceptions.ParseException;
import seedu.awe.model.group.GroupName;
import seedu.awe.model.person.Name;
import seedu.awe.model.person.Person;
import seedu.awe.testutil.ModelBuilder;

public class GroupRemoveContactCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_GROUPREMOVECONTACTCOMMAND_USAGE);
    private static final String MESSAGE_GROUP_NAME_INVALID = GroupName.MESSAGE_CONSTRAINTS;
    private static final Person[] membersNotInGroup = {ELLE, FIONA, GEORGE};
    private static final Person[] membersInGroup = {BOB, ALICE, AMY};
    private static final Person[] nonexistentMembers = {NONEXISTENTPERSON, BOB};
    private static final ArrayList<Person> GROUP_MEMBERS_NOT_IN_GROUP =
            new ArrayList<>(Arrays.asList(membersNotInGroup));
    private static final ArrayList<Person> GROUP_MEMBERS_IN_GROUP = new ArrayList<>(Arrays.asList(membersInGroup));
    private static final ArrayList<Person> NONEXISTENT_MEMBERS = new ArrayList<>(Arrays.asList(nonexistentMembers));
    private static final ObservableList<Person> MEMBERS_IN_MODEL =
            new ModelBuilder().build().getAwe().getPersonList();
    private GroupRemoveContactCommandParser parser = new GroupRemoveContactCommandParser(new ModelBuilder().build());


    @Test
    public void parse_missingParts_failure() {
        // no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);

        // empty input
        assertParseFailure(parser, " ", MESSAGE_INVALID_FORMAT);

        // only 1 group name specified
        assertParseFailure(parser, " gn/London", MESSAGE_INVALID_FORMAT);

        // only 1 member name specified
        assertParseFailure(parser, " n/" + VALID_NAME_BOB, MESSAGE_INVALID_FORMAT);

        // empty gn tag
        assertParseFailure(parser, " gn/ n/" + VALID_NAME_BOB, MESSAGE_GROUP_NAME_INVALID);

        // preambleEmpty
        assertParseFailure(parser, PREAMBLE_NON_EMPTY, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // 1 gn/ tag, 1 n/ tag but no group name
        assertParseFailure(parser, " gn/ n/" + VALID_NAME_BOB, MESSAGE_GROUP_NAME_INVALID);

        // 1 gn/ tag, 1 n/ tag but no member name
        assertThrows(EmptyGroupException.class, MESSAGE_CREATEGROUPCOMMAND_EMPTY_GROUP
                + MESSAGE_CREATEGROUPCOMMAND_INVALID_NAMES, () -> parser.parse(" gn/London n/"));
    }

    @Test
    public void parse_invalidArguments_failure() {
        // invalid group name
        assertParseFailure(parser, " gn/* n/" + VALID_NAME_BOB, MESSAGE_GROUP_NAME_INVALID);

        // invalid member name
        assertThrows(EmptyGroupException.class, () -> parser.parse(" gn/Bali n/%s"));
    }

    @Test
    public void parse_validGroupName_success() {
        GroupRemoveContactCommand expectedCommand = new GroupRemoveContactCommand(new GroupName("Bali"),
                GROUP_MEMBERS_NOT_IN_GROUP, true);
        assertParseSuccess(parser, " gn/Bali n/Elle Meyer n/Fiona Kunz n/George Best", expectedCommand);

        //reset parser
        parser = new GroupRemoveContactCommandParser(new ModelBuilder().build());

        GroupRemoveContactCommand expectedCommandDuplicatePerson = new GroupRemoveContactCommand(new GroupName("Bali"),
                GROUP_MEMBERS_IN_GROUP, true);
        assertParseSuccess(parser, " gn/Bali n/"
                + VALID_NAME_BOB + " n/"
                + VALID_NAME_AMY + " n/"
                + VALID_NAME_ALICE, expectedCommandDuplicatePerson);

        //reset parser
        parser = new GroupRemoveContactCommandParser(new ModelBuilder().build());

        // more than 1 group name
        GroupRemoveContactCommand expectedCommandDuplicateGroup = new GroupRemoveContactCommand(new GroupName("Bali"),
                GROUP_MEMBERS_NOT_IN_GROUP, true);
        assertParseSuccess(parser, " gn/Japan gn/Bali n/Elle Meyer n/Fiona Kunz n/George Best",
                expectedCommandDuplicateGroup);
    }

    @Test
    public void findMember_validMember_success() {
        //find valid member
        Person bob = GroupRemoveContactCommandParser.findMember(BOB.getName(), MEMBERS_IN_MODEL);
        assertEquals(BOB, bob);
    }

    @Test
    public void findMember_invalidMember_failure() {
        //find invalid member
        Person invalidMember = GroupRemoveContactCommandParser
                .findMember(NONEXISTENTPERSON.getName(), MEMBERS_IN_MODEL);
        assertEquals(null, invalidMember);
    }

    @Test
    public void addMemberToRemoveList_validMember_success() throws ParseException {
        boolean successfullyAdded = parser.addMemberToRemoveList(CARL.getName());
        assertEquals(true, successfullyAdded);
    }

    @Test
    public void addMemberToRemoveList_invalidMember_failure() throws ParseException {
        assertThrows(ParseException.class, Messages.MESSAGE_GROUPREMOVECONTACTCOMMAND_NONEXISTENT_PERSON, () ->
                parser.addMemberToRemoveList(NONEXISTENTPERSON.getName()));
    }

    @Test
    public void findMembersToBeRemoved_validMembers_success() throws ParseException {
        //parsing members that are in AWE contact list but not in group
        List<Name> membersNotInGroup = GROUP_MEMBERS_NOT_IN_GROUP
                .stream()
                .map(x -> x.getName())
                .collect(Collectors.toList());
        ArrayList<Person> newMembers = parser.findMembersToBeRemoved(membersNotInGroup);
        assertEquals(GROUP_MEMBERS_NOT_IN_GROUP, newMembers);

        //reset parser
        parser = new GroupRemoveContactCommandParser(new ModelBuilder().build());

        //parsing members that are in AWE contact list and some of them are already in the group
        List<Name> membersInGroup = GROUP_MEMBERS_IN_GROUP
                .stream()
                .map(x -> x.getName())
                .collect(Collectors.toList());
        ArrayList<Person> newMembersDuplicate = parser.findMembersToBeRemoved(membersInGroup);
        assertEquals(GROUP_MEMBERS_IN_GROUP, newMembersDuplicate);
    }

    @Test
    public void findMembersToBeRemoved_invalidMembers_failure() throws ParseException {
        List<Name> invalidMembers = NONEXISTENT_MEMBERS
                .stream()
                .map(x -> x.getName())
                .collect(Collectors.toList());
        assertThrows(ParseException.class, Messages.MESSAGE_GROUPREMOVECONTACTCOMMAND_NONEXISTENT_PERSON, () ->
                parser.findMembersToBeRemoved(invalidMembers));
    }
}
