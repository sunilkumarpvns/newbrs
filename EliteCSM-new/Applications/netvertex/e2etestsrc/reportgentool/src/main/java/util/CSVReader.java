package util;

import com.sterlite.pcc.Logger;
import com.sterlite.pcc.parsing.csv.CsvData;
import com.sterlite.pcc.parsing.csv.CsvHeader;
import com.sterlite.pcc.parsing.csv.CsvLine;
import com.sterlite.pcc.parsing.csv.CsvResult;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

/**
 * Created by aditya on 16-09-17.
 */
public class CSVReader {

    private static Logger log = new Logger();

    private CSVReader(){

    }

    public static CsvResult processInputFile(String inputFilePath) {
        return processInputFile(inputFilePath,true);
    }

    public static CsvResult processInputFile(String inputFilePath, boolean firstRecordAsHeader) {
        File inputF = new File(inputFilePath);
        if(inputF.exists() == false) {
            log.warn(inputF.getAbsolutePath() + " not found");
            return null;
        }

        try(InputStream inputFS = new FileInputStream(inputF);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS))){

            CSVParser parse;
            if(firstRecordAsHeader){
                 parse = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(br);
            }else{
                 parse = CSVFormat.RFC4180.parse(br);
            }
            CsvHeader csvHeader = null;
            if(Objects.nonNull(parse.getHeaderMap())) {
                List<String> headers = parse.getHeaderMap().
                    keySet().stream().collect(Collectors.toList());
                csvHeader = new CsvHeader(headers);
            }

            List<CSVRecord> records = parse.getRecords();

            List<CsvLine> collect = records.stream().map(CsvLine::fromCsvRecord).collect(toList());

            return new CsvResult(csvHeader, new CsvData(collect));

        } catch (Exception e) {
            log.error("error reading file: "+inputFilePath);
            e.printStackTrace();
        }
        return null;
    }




}
