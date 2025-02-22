package seedu.awe.logic.parser;

import static seedu.awe.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.awe.commons.core.Messages.MESSAGE_LISTTRANSACTIONSUMMARYCOMMAND_USAGE;
import static seedu.awe.logic.commands.CommandTestUtil.GROUPNAME_DESC_BALI;
import static seedu.awe.logic.commands.CommandTestUtil.GROUPNAME_DESC_OSLO;
import static seedu.awe.logic.commands.CommandTestUtil.INVALID_GROUP_NAME_DESC;
import static seedu.awe.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.awe.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.awe.logic.parser.CliSyntax.PREFIX_GROUP_NAME;
import static seedu.awe.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.awe.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.awe.testutil.Assert.assertThrows;
import static seedu.awe.testutil.TypicalGroups.BALI;
import static seedu.awe.testutil.TypicalGroups.OSLO;

import org.junit.jupiter.api.Test;

import seedu.awe.logic.commands.ListTransactionSummaryCommand;
import seedu.awe.logic.parser.exceptions.ParseException;
import seedu.awe.model.group.GroupName;




public class ListTransactionSummaryCommandParserTest {

    private ListTransactionSummaryCommandParser parser;

    public ListTransactionSummaryCommandParserTest() {
        parser = new ListTransactionSummaryCommandParser();
    }

    /**
     * Resets parser. Necessary as ListTransactionSummaryCommand parser needs to be initialized with a model for
     * each call. Failure to reset parser will result in Duplicate exceptions being raised.
     */
    public void resetParser() {
        parser = new ListTransactionSummaryCommandParser();
    }

    @Test
    public void parse_allFieldsPresent_success() {

        // regular input for ListTransactionSummaryCommand
        assertParseSuccess(parser, GROUPNAME_DESC_BALI,
                new ListTransactionSummaryCommand(BALI));

        resetParser();
        // names in different order
        assertParseSuccess(parser, GROUPNAME_DESC_OSLO,
                new ListTransactionSummaryCommand(OSLO));

        resetParser();
        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + GROUPNAME_DESC_BALI,
                new ListTransactionSummaryCommand(BALI));

        resetParser();
        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + GROUPNAME_DESC_OSLO,
                new ListTransactionSummaryCommand(OSLO));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                MESSAGE_LISTTRANSACTIONSUMMARYCOMMAND_USAGE);

        resetParser();
        // missing group prefix
        String inputMissingGroupPrefix = " Bali";
        assertParseFailure(parser, inputMissingGroupPrefix, expectedMessage);

        resetParser();
        // empty command
        assertParseFailure(parser, " ", expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                MESSAGE_LISTTRANSACTIONSUMMARYCOMMAND_USAGE);
        resetParser();
        // invalid group name
        assertParseFailure(parser, INVALID_GROUP_NAME_DESC, GroupName.MESSAGE_CONSTRAINTS);

        resetParser();
        // empty group name
        String emptyGroupNameInput = " " + PREFIX_GROUP_NAME;
        assertParseFailure(parser, emptyGroupNameInput, GroupName.MESSAGE_CONSTRAINTS);

        resetParser();
        // invalid name returns IllegalArgumentException
        String userInput = INVALID_GROUP_NAME_DESC;
        assertThrows(ParseException.class, GroupName.MESSAGE_CONSTRAINTS, () -> parser.parse(userInput));

        resetParser();
        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + GROUPNAME_DESC_BALI, expectedMessage);
    }
}
