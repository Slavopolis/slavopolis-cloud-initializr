import img1 from 'src/assets/images/profile/user-1.jpg';
import img2 from 'src/assets/images/profile/user-2.jpg';
import img3 from 'src/assets/images/profile/user-3.jpg';
import img4 from 'src/assets/images/profile/user-4.jpg';

interface PerformerType {
  id: string;
  imgsrc: string;
  name: string;
  post: string;
  pname: string;
  status: string;
  budget: string;
}

const TopPerformerData: PerformerType[] = [
  {
    id: '1',
    imgsrc: img1,
    name: 'Sunil Joshi',
    post: '网页设计师',
    pname: 'Elite Admin',
    status: '低',
    budget: '3.9',
  },
  {
    id: '2',
    imgsrc: img2,
    name: 'John Deo',
    post: '网页开发者',
    pname: 'Flexy Admin',
    status: '中',
    budget: '24.5',
  },
  {
    id: '3',
    imgsrc: img3,
    name: 'Mathew Anderson',
    post: '网站经理',
    pname: 'Material Pro',
    status: '高',
    budget: '12.8',
  },
  {
    id: '4',
    imgsrc: img4,
    name: 'Yuvraj Sheth',
    post: '项目经理',
    pname: 'Xtreme Admin',
    status: '非常高',
    budget: '2.4',
  },
];

export default TopPerformerData;
