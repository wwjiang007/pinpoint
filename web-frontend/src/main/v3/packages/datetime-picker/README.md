# 🍝 Rich Datetime Picker

## Installation

## How to use

```tsx
import 'rich-datetime-picker/dist/rich-datetime-picker.css';
import { RichDatetimePicker } from 'rich-datetime-picker';

/** if you want to override style via cssModule */
import css from './RichDatetimePicker.module.scss';
import classNames from 'classnames/bind';

const cx = classNames.bind(css);


const now = new Date();
const [startDate, setStartDate] = React.useState<Date | null>(subMinutes(now, 5));
const [endDate, setEndDate] = React.useState<Date | null>(now);

const handleChange = (params: DateRange) => {
  setStartDate(params[0]);
  setEndDate(params[1]);
};

return (
   <RichDatetimePicker
    /** if you want to override style via cssModule */
    className={cx('datePickerContainer')}
    triggerClassName={cx('datePickerTrigger')}
    startDate={startDate}
    endDate={endDate}
    onChange={handleChange}
  />;
)
```

## Props

| Props                   | Type                                                                                | Default                                                                                                                                                                                                                        | Description                                                                                                          |
| ----------------------- | ----------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------- |
| startDate               | Date \| null                                                                        |                                                                                                                                                                                                                                | Date of start                                                                                                        |
| endDate                 | Date \| null                                                                        |                                                                                                                                                                                                                                | Date of end                                                                                                          |
| className               | string                                                                              |                                                                                                                                                                                                                                | ClassName of container                                                                                               |
| inputClassName          | string                                                                              |                                                                                                                                                                                                                                | ClassName of input                                                                                                   |
| triggerClassName        | string                                                                              |                                                                                                                                                                                                                                | ClassName of trigger                                                                                                 |
| panelClassName          | string                                                                              |                                                                                                                                                                                                                                | ClassName of panel                                                                                                   |
| datePickerClassName     | string                                                                              |                                                                                                                                                                                                                                | ClassName of react-datepicker                                                                                        |
| displayedInput          | string                                                                              |                                                                                                                                                                                                                                | Set the displayed Input value directly.                                                                              |
| minDate                 | Date                                                                                |                                                                                                                                                                                                                                | Minimum date that the user can select.                                                                               |
| maxDate                 | Date                                                                                |                                                                                                                                                                                                                                | Max date that the user can select.                                                                                   |
| localeKey               | 'en' \| 'ko'                                                                        | 'en'                                                                                                                                                                                                                           | Key of locale that should be used by the datetime picker and the calendar.                                           |
| timeZone                | string ([IANA time zone information](https://nodatime.org/TimeZones))               | `Intl.DateTimeFormat().resolved.timeZone`                                                                                                                                                                                      | Key of locale that should be used by the datetime picker and the calendar.                                           |
| dateFormat              | string                                                                              | 'MMM do, hh:mm a'                                                                                                                                                                                                              | Input format based on [Unicode tokens](https://www.unicode.org/reports/tr35/tr35-dates.html#Date_Field_Symbol_Table) |
| seamToken               | string                                                                              | '-'                                                                                                                                                                                                                            | Token connecting between dates                                                                                       |
| disable                 | boolean                                                                             |                                                                                                                                                                                                                                | Disable input trigger                                                                                                |
| defaultOpen             | boolean                                                                             |                                                                                                                                                                                                                                | Determining whether to open or not initially                                                                         |
| hideCalendarYearButton  | boolean                                                                             | false                                                                                                                                                                                                                          | Hide/display year change buttons                                                                                     |
| timeUnits               | TimeUnitFormat[]                                                                    | ['5m', '20m', '1h', '3h', '6h', '12h', '1d', '2d']                                                                                                                                                                             | Time units for setting the time quickly                                                                              |
| children                | ((props: RichDatetimePickerListItemProps[]) => React.ReactNode) \| React.ReactNode; |                                                                                                                                                                                                                                | Content to be included in the Panel of time units. You can freely include a variety of elements                      |
| customTimeView          | React.ReactNode                                                                     |                                                                                                                                                                                                                                | Content to be included in the CustomTimView. You can freely include a variety of elements                            |
| customTimeViewDirection | 'right' \| 'left'                                                                   |                                                                                                                                                                                                                                | Direction of CustomTimeView                                                                                          |
| customTimes             | [key: string]: string[]                                                             | `Relative: ['45m', '12hours', '10d', '2 weeks', 'last month', 'yesterday', 'today']` <br> `Fixed: ['Sep 1', 'Sep 1 - Sep 2', '9/1', '9/1 - 9/2', '09:00 AM - 05:00 PM']` <br> `Unix timestamps: 1693728134007 - 1693728434007` | Rendering to standardized specifications. The key becomes the subheader, and the array becomes the tag.              |
| formatTag               | (ms: number) => string;                                                             |                                                                                                                                                                                                                                | Format time unit tag                                                                                                 |
| formatListItemName      | (ms: number) => string;                                                             |                                                                                                                                                                                                                                | Format name of the quick list item                                                                                   |
| onChange                | (params: DateRange, text: string, timeUnit?: string) => void;                       |                                                                                                                                                                                                                                | Function called when the user picks a valid datetime or enter a valid datestring.                                    |
| validateDatePickerRange | (params: DateRange) => boolean;                                                     | () => true                                                                                                                                                                                                                     | Check date range validation in calendar                                                                              |
| getPanelContainer       | () => HTMLElement \| null;                                                          |                                                                                                                                                                                                                                | Renders the panel to the specified element.                                                                          |