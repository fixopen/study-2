package com.baremind.data;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fixopen on 22/11/2016.
 */
public class EnglishBook { //book
    private String type; //@type
    private String no; //@id
    private String name; //@name
    public static class Page {
        public static class Unit {
            public static class Rectangle {
                int left;
                int top;
                int right;
                int bottom;

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }

                public int getRight() {
                    return right;
                }

                public void setRight(int right) {
                    this.right = right;
                }

                public int getBottom() {
                    return bottom;
                }

                public void setBottom(int bottom) {
                    this.bottom = bottom;
                }
            }
            String no; //unitno
            Rectangle bounds = new Rectangle(); //rect
            String content; //url

            public String getNo() {
                return no;
            }

            public void setNo(String no) {
                this.no = no;
            }

            public Rectangle getBounds() {
                return bounds;
            }

            public void setBounds(Rectangle bounds) {
                this.bounds = bounds;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
        String no; //page
        List<Unit> units = new ArrayList<>(); //unit

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public List<Unit> getUnits() {
            return units;
        }

        public void setUnits(List<Unit> units) {
            this.units = units;
        }
    }
    private List<Page> pages = new ArrayList<>(); //item

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubjectNo() {
        return type.substring(0, 2);
    }

    public String getGradeNo() {
        return type.substring(2);
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public static class Builder extends DefaultHandler {
        private EnglishBook data;
        private EnglishBook.Page currentPage;
        private EnglishBook.Page.Unit currentUnit;

        private String currentTag;
        private String currentData = "";
        private byte[] contents;

        public Builder(byte[] contents) {
            this.contents = contents;
        }

        public void startDocument() {
        }


        public void endDocument() {
        }


        public void startElement(String uri, String name, String qName, Attributes atts) {
            currentTag = name;
            currentData = "";
            switch (currentTag) {
                case "book":
                    //new book
                    data = new EnglishBook();
                    for (int i = 0; i < atts.getLength(); ++i) {
                        String n = atts.getLocalName(i);
                        String v = atts.getValue(i);
                        switch (n) {
                            case "type":
                                data.type = v;
                                break;
                            case "id":
                                data.no = v;
                                break;
                            case "name":
                                data.name = v;
                                break;
                        }
                    }
                    break;
                case "item":
                    //new page, append to pages
                    currentPage = new EnglishBook.Page();
                    data.pages.add(currentPage);
                    break;
                case "unit":
                    //new unit, append to units
                    currentUnit = new EnglishBook.Page.Unit();
                    currentPage.units.add(currentUnit);
                    break;
            }
        }


        public void endElement(String uri, String name, String qName) {
            currentTag = "";
            currentData = "";
        }

        public void characters(char ch[], int start, int length) {
            currentData += new String(ch, start, length);
            switch (currentTag) {
                case "page":
                    currentPage.no = currentData;
                    break;
                case "unitno":
                    currentUnit.no = currentData;
                    break;
                case "rect":
                    currentUnit.bounds = convertToRectangle(currentData);
                    break;
                case "url":
                    currentUnit.content = currentData;
                    break;
                default:
                    break;
            }
        }

        private EnglishBook.Page.Unit.Rectangle convertToRectangle(String v) {
            EnglishBook.Page.Unit.Rectangle result = new EnglishBook.Page.Unit.Rectangle();
            String[] vs = v.split(",");
            if (vs.length == 4) {
                result.left = Integer.parseInt(vs[0]);
                result.top = Integer.parseInt(vs[1]);
                result.right = Integer.parseInt(vs[2]);
                result.bottom = Integer.parseInt(vs[3]);
            }
            return result;
        }

        public EnglishBook build() {
            try {
                XMLReader xr = XMLReaderFactory.createXMLReader();
                xr.setContentHandler(this);
                xr.setErrorHandler(this);
                try {
                    xr.parse(new InputSource(new ByteArrayInputStream(contents)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (SAXException e) {
                e.printStackTrace();
            }
            return data;
        }
    }
}

