package org.example;

import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FileUploader {

    // путь для загрузки файлов
    private static final String UPLOAD_DIRECTORY = "upload";

    // параметры загрузки
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

    public static List<NameValuePair> doPost(Request request) {

        // настраиваем параметры загрузки
        final var factory = new DiskFileItemFactory();

        // устанавливаем максимальный размер файла в памяти, после которого он сохраняется на диск
        factory.setSizeThreshold(MEMORY_THRESHOLD);

        // директория для временного хранения файлов
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        // создаем обработчик загрузки
        final var upload = new ServletFileUpload(factory);

        // устанавливаем максимальный размер загружаемого файла
        upload.setFileSizeMax(MAX_FILE_SIZE);

        // устанавливаем максимальный размер запроса включая файлы и данные из форм
        upload.setSizeMax(MAX_REQUEST_SIZE);

        // создаем путь к директории для загрузки файлов
        // этот путь относительный в директории приложения
        final var uploadPath = UPLOAD_DIRECTORY;

        // создаем директорию, если ее не существует
        final var uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        final var postParams = new ArrayList<NameValuePair>();

        try {
            // парсим запрос
            final var formItems = upload.parseRequest(request);

            if (formItems != null && formItems.size() > 0) {
                // проходим по эленментам
                for (var item : formItems) {
                    // обрабатываем формы
                    if (item.isFormField()) {
                        postParams.add(new BasicNameValuePair(item.getFieldName(), item.getString()));
                    }
                    // обрабатыеваем файлы
                    if (!item.isFormField()) {
                        final var fileName = new File(item.getName()).getName();
                        if (!fileName.equals("")) {
                            final var filePath = uploadPath + File.separator + fileName;
                            final var storeFile = new File(filePath);
                            // сохраняем файл на диск
                            item.write(storeFile);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postParams;
    }

}