package org.sagebionetworks.dashboard.parse;

public class QuestionIndexReader implements RecordReader<CuResponseRecord, String> {

    @Override
    public String read(CuResponseRecord record) {
        return Integer.toString(record.questionIndex());
    }
}
