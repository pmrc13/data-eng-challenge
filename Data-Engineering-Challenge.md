# Data Engineering Challenge

The goal of this challenge is to build a solution that processes Dublin Bus dataset and emit events within certain conditions.

With a technology of your choice and a language of your choice, replay all imported data into a message queue or any infra that would allow to listen to an event stream (please explain what you chose and why).

Your app should calculate the [cumulative moving average (CMA)](https://en.wikipedia.org/wiki/Moving_average#Cumulative_moving_average) for each bus, every 30 seconds. If the result of the CMA is greater or equal to 20 kilometers per hour, it should emit an alert. This alert should be delivered into a message queue system. Example of an alert:

```json
{
    "timestampStart": 0,
    "timestampFinish": 1,
    "vehicleId": "09876",
    "eventTimestamp": 2,
    "cumAvgSpeed": 23.32
 }
```

- `timestampStart`: first event timestamp within 30 second period. In seconds (unix timestamp).
- `timestampFinish`: last event timestamp within 30 second period. In seconds (unix timestamp).
- `vehicleId`: vehicle identifier. Prefer string. 
- `eventTimestamp`: timestamp when the alert was triggered. In seconds (unix timestamp).
- `cumAvgSpeed`: cumulative moving average speed. In kilometer per hour.

## What we expect

We expect to see a scalable solution that could accommodate large datasets. We also expect you to deliver a solution that you would consider minimum viable product (MVP) and could be eventually integrated into a larger system, with some reasoning on how it could be achieved.

Before implementing this exercise, you need to:

- Choose a message queue system that is appropriate;
- Choose a programming language that is type-safe and you are familiar with;

## What to deliver

- A data loader script/app;
- A data streaming processor app (or all together, as you prefer);
- Instructions on how to load the dataset into the message queue and start the streaming app;
- Documentation and/or examples on how to use and/or test. Do not forget to write down assumptions you made.

The implementation MUST be delivered using this repo and it should run on [Docker](https://www.docker.com/). Other files (e.g. large files or csv data) MAY be outside.

You have one week to deliver the solution.

## Dataset to use

You may find the datasets here:
Name: [Dublin Bus GPS sample data from Dublin City Council](https://data.gov.ie/dataset/dublin-bus-gps-sample-data-from-dublin-city-council-insight-project
)

URL: https://data.gov.ie/dataset/dublin-bus-gps-sample-data-from-dublin-city-council-insight-project

Download "From 6th Nov 2012 to 30th Nov 2012" dataset, extract, and from that extraction use all csv files as input.

Note: This data is public and not owned by us, and may be subject to restrictions and limitations as described by the Dublin City Council, in the dataset's own license section. For more details on the dataset and data types contained therein, you can check the description on the website.
