package com.joboffers;

public interface SampleJobOfferResponse {
    default String bodyWithZeroOffersJson() {
        //return "[]";
        return "{ \"postings\": [] }";
    }

    default String bodyWithOneOfferJson() {
        return """
                [
                {
                    "title": "Software Engineer - Mobile (m/f/d)",
                    "company": "Cybersource",
                    "salary": "4k - 8k PLN",
                    "offerUrl": "https://nofluffjobs.com/pl/job/software-engineer-mobile-m-f-d-cybersource-poznan-entavdpn"
                }
                ]
                """.trim();
    }

    default String bodyWithTwoOffersJson() {
//        return """
//                [
//                {
//                    "title": "Software Engineer - Mobile (m/f/d)",
//                    "company": "Cybersource",
//                    "salary": "4k - 8k PLN",
//                    "offerUrl": "https://nofluffjobs.com/pl/job/software-engineer-mobile-m-f-d-cybersource-poznan-entavdpn"
//                },
//                {
//                    "title": "Junior DevOps Engineer",
//                    "company": "CDQ Poland",
//                    "salary": "8k - 14k PLN",
//                    "offerUrl": "https://nofluffjobs.com/pl/job/junior-devops-engineer-cdq-poland-wroclaw-gnymtxqd"
//                }
//                ]
//                """.trim();
        return """
            {
                \"postings\": [
                    {
                        "name": "Cybersource",
                        "title": "Software Java Engineer - Mobile (m/f/d)",
                        "seniority": ["junior"],
                        "url": "software-engineer-mobile-m-f-d-cybersource-warsaw-entavdpn",
                        "salary": {
                            "from": 4000,
                            "to": 8000,
                            "currency": "PLN"
                        }
                    },
                    {
                        "name": "CDQ Poland",
                        "title": "Junior Java Engineer",
                        "seniority": ["junior"],
                        "url": "junior-devops-engineer-cdq-poland-warszawa-gnymtxqd",
                        "salary": {
                            "from": 8000,
                            "to": 14000,
                            "currency": "PLN"
                        }
                    }
                ]
            }
            """.trim();
    }

    default String bodyWithFourOffersJson() {
        return """
            {
                "postings": [
                    {
                        "name": "Cybersource",
                        "title": "Software Java Engineer - Mobile (m/f/d)",
                        "seniority": ["junior"],
                        "url": "software-engineer-mobile-m-f-d-cybersource-warsaw-entavdpn",
                        "salary": {
                            "from": 4000,
                            "to": 8000,
                            "currency": "PLN"
                        }
                    },
                    {
                        "name": "CDQ Poland",
                        "title": "Junior Java Engineer",
                        "seniority": ["junior"],
                        "url": "junior-devops-engineer-cdq-poland-warszawa-gnymtxqd",
                        "salary": {
                            "from": 8000,
                            "to": 14000,
                            "currency": "PLN"
                        }
                    },
                    {
                        "name": "Sollers Consulting",
                        "title": "Junior Java Developer",
                        "seniority": ["junior"],
                        "url": "junior-java-developer-sollers-consulting-warszawa-s6et1ucc",
                        "salary": {
                            "from": 7500,
                            "to": 11500,
                            "currency": "PLN"
                        }
                    },
                    {
                        "name": "Vertabelo S.A.",
                        "title": "Junior Full Stack Java Developer",
                        "seniority": ["junior"],
                        "url": "junior-full-stack-java-developer-vertabelo-remote-k7m9xpnm",
                        "salary": {
                            "from": 7000,
                            "to": 9000,
                            "currency": "PLN"
                        }
                    }
                ]
            }
            """.trim();
//        return """
//                [
//                {
//                "title": "Software Engineer - Mobile (m/f/d)",
//                "company": "Cybersource",
//                "salary": "4k - 8k PLN",
//                "offerUrl": "https://nofluffjobs.com/pl/job/software-engineer-mobile-m-f-d-cybersource-poznan-entavdpn"
//                },
//                {
//                "title": "Junior DevOps Engineer",
//                "company": "CDQ Poland",
//                "salary": "8k - 14k PLN",
//                "offerUrl": "https://nofluffjobs.com/pl/job/junior-devops-engineer-cdq-poland-wroclaw-gnymtxqd"
//                },
//                {
//                "title": "Junior Java Developer",
//                "company": "Sollers Consulting",
//                "salary": "7 500 - 11 500 PLN",
//                "offerUrl": "https://nofluffjobs.com/pl/job/junior-java-developer-sollers-consulting-warszawa-s6et1ucc"
//                },
//                {
//                "title": "Junior Full Stack Developer",
//                "company": "Vertabelo S.A.",
//                "salary": "7 000 - 9 000 PLN",
//                "offerUrl": "https://nofluffjobs.com/pl/job/junior-full-stack-developer-vertabelo-remote-k7m9xpnm"
//                }
//                ]
//                """.trim();
    }
}
