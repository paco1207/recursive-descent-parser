import java.io.IOException;

public class SyntaxAnalyser extends AbstractSyntaxAnalyser {


    public SyntaxAnalyser(String fileName) throws IOException {
        // Initialize the lexical analyser
        this.lex = new LexicalAnalyser(fileName);
    }

    @Override
    public void _statementPart_() throws IOException, CompilationException {
        // Start process StatementPart
        this.myGenerate.commenceNonterminal("StatementPart");
        acceptTerminal(Token.beginSymbol);
        statementList();
        acceptTerminal(Token.endSymbol);
        this.myGenerate.finishNonterminal("StatementPart");
    }

    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {
        if (this.nextToken.symbol == symbol) {
            // Next token matches with expected symbol, insert token to terminal
            this.myGenerate.insertTerminal(this.nextToken);
            // Read the next token
            this.nextToken = this.lex.getNextToken();
        } else {
            // Next token does not match with expected symbol, need to process error
            this.myGenerate.reportError(this.nextToken, "Expected " + Token.getName(symbol) +
                    ", found " + this.nextToken.text + ", at line " + this.nextToken.lineNumber);
        }
    }

    /*
    As <StatementList> ::= <Statement> | <StatementList> ; <Statement> is Left Recursive, update is required
    =>
        <StatementList> ::= <Statement> { ; <Statement> }
     */
    private void statementList() throws IOException, CompilationException {
        // Start process StatementList
        this.myGenerate.commenceNonterminal("StatementList");
        statement();
        // Check if more statement list is required
        while (this.nextToken.symbol == Token.semicolonSymbol) {
            acceptTerminal(Token.semicolonSymbol);
            statement();
        }
        this.myGenerate.finishNonterminal("StatementList");
    }

    private void statement() throws IOException, CompilationException {
        // Start process Statement
        this.myGenerate.commenceNonterminal("Statement");
        switch (this.nextToken.symbol) {
            case Token.identifier:
                assignmentStatement();
                break;
            case Token.ifSymbol:
                ifStatement();
                break;
            case Token.whileSymbol:
                whileStatement();
                break;
            case Token.callSymbol:
                procedureStatement();
                break;
            case Token.untilSymbol:
                untilStatement();
                break;
            case Token.forSymbol:
                forStatement();
                break;
            default:
                this.myGenerate.reportError(this.nextToken, "Expected starting symbol for statement" +
                        ", found " + this.nextToken.text + ", at line " + this.nextToken.lineNumber);
        }
        this.myGenerate.finishNonterminal("Statement");
    }

    /*
    For AssignmentStatement, Left-Factoring is required
    =>
        <AssignmentStatement> ::= identifier := <AssignmentStatementRemainder>
        <AssignmentStatementRemainder> ::= <Expression> | stringConstant
     */
    private void assignmentStatement() throws IOException, CompilationException {
        // Start process AssignmentStatement
        this.myGenerate.commenceNonterminal("AssignmentStatement");
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.becomesSymbol);
        assignmentStatementRemainder();
        this.myGenerate.finishNonterminal("AssignmentStatement");
    }

    private void assignmentStatementRemainder() throws IOException, CompilationException {
        if (this.nextToken.symbol == Token.stringConstant) {
            acceptTerminal(Token.stringConstant);
        } else {
            expression();
        }
    }

    /*
    For IfStatement, Left-Factoring is required
    =>
        <IfStatement> ::= if <Condition> then <StatementList> <IfStatementRemainder>
        <IfStatementRemainder> ::= else <StatementList> end if | end if
     */
    private void ifStatement() throws IOException, CompilationException {
        // Start process IfStatement
        this.myGenerate.commenceNonterminal("IfStatement");
        acceptTerminal(Token.ifSymbol);
        condition();
        acceptTerminal(Token.thenSymbol);
        statementList();
        ifStatementRemainder();
        this.myGenerate.finishNonterminal("IfStatement");
    }

    private void ifStatementRemainder() throws IOException, CompilationException {
        if (this.nextToken.symbol == Token.elseSymbol) {
            // Process else block
            acceptTerminal(Token.elseSymbol);
            statementList();
        }
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.ifSymbol);
    }

    private void whileStatement() throws IOException, CompilationException {
        // Start process WhileStatement
        this.myGenerate.commenceNonterminal("WhileStatement");
        acceptTerminal(Token.whileSymbol);
        condition();
        acceptTerminal(Token.loopSymbol);
        statementList();
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        this.myGenerate.finishNonterminal("WhileStatement");
    }

    private void procedureStatement() throws IOException, CompilationException {
        // Start process ProcedureStatement
        this.myGenerate.commenceNonterminal("ProcedureStatement");
        acceptTerminal(Token.callSymbol);
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.leftParenthesis);
        argumentList();
        acceptTerminal(Token.rightParenthesis);
        this.myGenerate.finishNonterminal("ProcedureStatement");
    }

    private void untilStatement() throws IOException, CompilationException {
        // Start process UntilStatement
        this.myGenerate.commenceNonterminal("UntilStatement");
        acceptTerminal(Token.doSymbol);
        statementList();
        acceptTerminal(Token.untilSymbol);
        condition();
        this.myGenerate.finishNonterminal("UntilStatement");
    }

    private void forStatement() throws IOException, CompilationException {
        // Start process ForStatement
        this.myGenerate.commenceNonterminal("ForStatement");
        acceptTerminal(Token.forSymbol);
        acceptTerminal(Token.leftParenthesis);
        assignmentStatement();
        acceptTerminal(Token.semicolonSymbol);
        condition();
        acceptTerminal(Token.semicolonSymbol);
        assignmentStatement();
        acceptTerminal(Token.rightParenthesis);
        acceptTerminal(Token.doSymbol);
        statementList();
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        this.myGenerate.finishNonterminal("ForStatement");
    }

    /*
    For ArgumentList, update is required as it has Left Recursion
    =>
        <ArgumentList> ::= identifier { , identifier }
     */
    private void argumentList() throws IOException, CompilationException {
        // Start process ArgumentList
        this.myGenerate.commenceNonterminal("ArgumentList");
        acceptTerminal(Token.identifier);
        while (this.nextToken.symbol == Token.commaSymbol) {
            acceptTerminal(Token.commaSymbol);
            acceptTerminal(Token.identifier);
        }
        this.myGenerate.finishNonterminal("ArgumentList");
    }

    /*
    For Condition, Left-Factoring is required
    =>
        <Condition> ::= identifier <ConditionalOperator> <ConditionRemainder>
        <ConditionRemainder> ::= identifier | numberConstant | stringConstant
     */
    private void condition() throws IOException, CompilationException {
        // Start process Condition
        this.myGenerate.commenceNonterminal("Condition");
        acceptTerminal(Token.identifier);
        conditionalOperator();
        conditionRemainder();
        this.myGenerate.finishNonterminal("Condition");
    }

    private void conditionRemainder() throws IOException, CompilationException {
        switch (this.nextToken.symbol) {
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
                break;
            case Token.stringConstant:
                acceptTerminal(Token.stringConstant);
                break;
            default:
                this.myGenerate.reportError(this.nextToken, "Expected one of " + Token.getName(Token.identifier) +
                        ", " + Token.getName(Token.numberConstant) + ", " + Token.getName(Token.stringConstant) +
                        ", found " + this.nextToken.text + ", at line " + this.nextToken.lineNumber);
        }
    }

    private void conditionalOperator() throws IOException, CompilationException {
        // Start process ConditionalOperator
        this.myGenerate.commenceNonterminal("ConditionalOperator");
        switch (this.nextToken.symbol) {
            case Token.greaterThanSymbol:
                acceptTerminal(Token.greaterThanSymbol);
                break;
            case Token.greaterEqualSymbol:
                acceptTerminal(Token.greaterEqualSymbol);
                break;
            case Token.equalSymbol:
                acceptTerminal(Token.equalSymbol);
                break;
            case Token.notEqualSymbol:
                acceptTerminal(Token.notEqualSymbol);
                break;
            case Token.lessThanSymbol:
                acceptTerminal(Token.lessThanSymbol);
                break;
            case Token.lessEqualSymbol:
                acceptTerminal(Token.lessEqualSymbol);
                break;
            default:
                this.myGenerate.reportError(this.nextToken, "Expected one of conditional operator symbol" +
                        ", found " + this.nextToken.text + ", at line " + this.nextToken.lineNumber);
        }
        this.myGenerate.finishNonterminal("ConditionalOperator");
    }

    /*
    For Expression, update is required due to Left Recursion
    =>
        <Expression> ::= <Term> { + <Term> | - <Term> }
     */
    private void expression() throws IOException, CompilationException {
        // Start process Expression
        this.myGenerate.commenceNonterminal("Expression");
        term();
        while (this.nextToken.symbol == Token.plusSymbol || this.nextToken.symbol == Token.minusSymbol) {
            if (this.nextToken.symbol == Token.plusSymbol) {
                acceptTerminal(Token.plusSymbol);
            } else {
                acceptTerminal(Token.minusSymbol);
            }
            term();
        }
        this.myGenerate.finishNonterminal("Expression");
    }

    /*
    For Term, update is required due to Left Recursion
    =>
        <Term> ::= <Factor> { * <Factor> | / <Factor> | % <Factor> }
     */
    private void term() throws IOException, CompilationException {
        // Start process Term
        this.myGenerate.commenceNonterminal("Term");
        factor();
        while (this.nextToken.symbol == Token.timesSymbol || this.nextToken.symbol == Token.divideSymbol || this.nextToken.symbol == Token.modSymbol) {
            if (this.nextToken.symbol == Token.timesSymbol) {
                acceptTerminal(Token.timesSymbol);
            } else if (this.nextToken.symbol == Token.divideSymbol) {
                acceptTerminal(Token.divideSymbol);
            } else {
                acceptTerminal(Token.modSymbol);
            }
            factor();
        }
        this.myGenerate.finishNonterminal("Term");
    }

    private void factor() throws IOException, CompilationException {
        // Start process Factor
        this.myGenerate.commenceNonterminal("Factor");
        switch (this.nextToken.symbol) {
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
                break;
            case Token.leftParenthesis:
                acceptTerminal(Token.leftParenthesis);
                expression();
                acceptTerminal(Token.rightParenthesis);
                break;
            default:
                this.myGenerate.reportError(this.nextToken, "Expected valid symbol for identifer, number constant, or expression" +
                        ", found " + this.nextToken.text + ", at line " + this.nextToken.lineNumber);
        }
        this.myGenerate.finishNonterminal("Factor");
    }

}
